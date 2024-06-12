package cn.evlight.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.evlight.domain.award.model.aggregate.DistributeAwardsAggregate;
import cn.evlight.domain.award.model.aggregate.UserAwardRecordAggregate;
import cn.evlight.domain.award.model.entity.CreditAwardEntity;
import cn.evlight.domain.award.model.entity.TaskEntity;
import cn.evlight.domain.award.model.entity.UserAwardRecordEntity;
import cn.evlight.domain.award.model.valobj.AccountStatusVO;
import cn.evlight.domain.award.repository.IAwardRepository;
import cn.evlight.infrastructure.event.EventPublisher;
import cn.evlight.infrastructure.persistent.dao.*;
import cn.evlight.infrastructure.persistent.po.*;
import cn.evlight.infrastructure.persistent.redis.IRedisService;
import cn.evlight.types.common.Constants;
import cn.evlight.types.exception.AppException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 抽奖仓库实现类
 * @Author: evlight
 * @Date: 2024/6/7
 */

@Slf4j
@Repository
public class AwardRepository implements IAwardRepository {

    @Resource
    private TaskMapper taskDao;
    @Resource
    private UserAwardRecordMapper userAwardRecordDao;
    @Resource
    private IDBRouterStrategy dbRouter;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private EventPublisher eventPublisher;

    @Autowired
    private UserRaffleOrderMapper userRaffleOrderMapper;

    @Autowired
    private UserCreditAccountMapper userCreditAccountMapper;

    @Autowired
    private IAwardDao awardDao;

    @Autowired
    private IRedisService redisService;

    @Override
    public void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate) {
        //用户中奖记录
        UserAwardRecordEntity userAwardRecordEntity = userAwardRecordAggregate.getUserAwardRecordEntity();
        UserAwardRecord userAwardRecord = new UserAwardRecord();
        userAwardRecord.setUserId(userAwardRecordEntity.getUserId());
        userAwardRecord.setActivityId(userAwardRecordEntity.getActivityId());
        userAwardRecord.setStrategyId(userAwardRecordEntity.getStrategyId());
        userAwardRecord.setOrderId(userAwardRecordEntity.getOrderId());
        userAwardRecord.setAwardId(userAwardRecordEntity.getAwardId());
        userAwardRecord.setAwardTitle(userAwardRecordEntity.getAwardTitle());
        userAwardRecord.setAwardTime(userAwardRecordEntity.getAwardTime());
        userAwardRecord.setAwardState(userAwardRecordEntity.getAwardState().getCode());
        //mq发送任务
        TaskEntity taskEntity = userAwardRecordAggregate.getTaskEntity();
        Task task = new Task();
        task.setUserId(taskEntity.getUserId());
        task.setTopic(taskEntity.getTopic());
        task.setMessageId(taskEntity.getMessageId());
        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
        task.setState(taskEntity.getState().getCode());
        //事务
        try {
            dbRouter.doRouter(userAwardRecordEntity.getUserId());
            transactionTemplate.execute(status -> {
                try {
                    //保存用户中奖记录
                    userAwardRecordDao.save(userAwardRecord);
                    //保存mq发送任务
                    taskDao.save(task);
                    //更新抽奖单状态
                    int updated = userRaffleOrderMapper.updateAfterRaffle(UserRaffleOrder.builder()
                            .userId(userAwardRecord.getUserId())
                            .orderId(userAwardRecord.getOrderId())
                            .build());
                    if(updated != 1){
                        //更新失败
                        status.setRollbackOnly();
                        throw new AppException(Constants.ExceptionInfo.RAFFLE_ORDER_REUSE);
                    }
                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("[保存用户中奖记录]唯一索引冲突 userId: {} activityId: {} awardId: {}", userAwardRecord.getUserId(), userAwardRecord.getActivityId(), userAwardRecord.getAwardId(), e);
                    throw new AppException(Constants.ExceptionInfo.DUPLICATE_KEY);
                }
            });
        } finally {
            dbRouter.clear();
        }
        //发送消息到mq
        try {
            eventPublisher.publish(task.getTopic(), taskEntity.getMessage());
            //发送成功更新任务状态
            log.info("[MQ]-[保存用户中奖记录]-[publisher] 发送成功 userId: {} topic: {}", userAwardRecord.getUserId(), task.getTopic());
            taskDao.updateAfterCompleted(task);
        } catch (Exception e) {
            //发送失败更新任务状态
            log.error("[MQ]-[保存用户中奖记录]-[publisher] 发送失败 userId: {} topic: {}", userAwardRecord.getUserId(), task.getTopic());
            taskDao.updateAfterFailed(task);
        }
    }

    @Override
    public void saveDistributeAwardsAggregate(DistributeAwardsAggregate distributeAwardsAggregate) {
        UserAwardRecordEntity userAwardRecordEntity = distributeAwardsAggregate.getUserAwardRecordEntity();
        CreditAwardEntity creditAwardEntity = distributeAwardsAggregate.getCreditAwardEntity();
        //中奖记录
        UserAwardRecord userAwardRecord = UserAwardRecord.builder()
                .userId(userAwardRecordEntity.getUserId())
                .activityId(userAwardRecordEntity.getActivityId())
                .strategyId(userAwardRecordEntity.getStrategyId())
                .orderId(userAwardRecordEntity.getOrderId())
                .awardId(userAwardRecordEntity.getAwardId())
                .awardTitle(userAwardRecordEntity.getAwardTitle())
                .awardTime(userAwardRecordEntity.getAwardTime())
                .awardState(userAwardRecordEntity.getAwardState().getCode())
                .build();
        //积分账户
        UserCreditAccount userCreditAccount = UserCreditAccount.builder()
                .userId(creditAwardEntity.getUserId())
                .totalAmount(creditAwardEntity.getCreditAmount())
                .availableAmount(creditAwardEntity.getCreditAmount())
                .accountStatus(AccountStatusVO.open.getCode())
                .build();
        //事务
        String lockKey = Constants.RedisKey.AWARD_CREDIT_ACCOUNT_LOCK + distributeAwardsAggregate.getUserId();
        RLock lock = redisService.getLock(lockKey);
        try {
            lock.lock(3, TimeUnit.SECONDS);
            dbRouter.doRouter(distributeAwardsAggregate.getUserId());
            transactionTemplate.execute(status -> {
                try {
                    //更新用户积分账户
                    int updated = userCreditAccountMapper.addAmount(userCreditAccount);
                    if(updated != 1){
                        //积分账户不存在 创建
                        userCreditAccountMapper.save(userCreditAccount);
                    }
                    //更新奖品记录
                    updated = userAwardRecordDao.updateAfterCompleted(userAwardRecord);
                    if(updated != 1){
                        log.warn("[更新用户中奖记录] 重复更新");
                    }
                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("[更新用户中奖记录] 唯一索引冲突");
                    throw new AppException(Constants.ExceptionInfo.DUPLICATE_KEY);
                }
            });
        } finally {
            dbRouter.clear();
            lock.unlock();
        }
    }

    @Override
    public String getAwardConfig(Integer awardId) {
        return awardDao.getAwardConfig(Award.builder()
                        .awardId(awardId)
                .build());
    }

    @Override
    public String getAwardKey(Integer awardId) {
        //先查询缓存
        String cacheKey = Constants.RedisKey.AWARD_KEY_KEY + awardId;
        String awardKey = redisService.getValue(cacheKey);
        if(awardKey != null) return awardKey;
        //再查询数据库
        awardKey = awardDao.getAwardKey(Award.builder()
                        .awardId(awardId)
                .build());
        redisService.setValue(cacheKey, awardKey);
        return awardKey;
    }

    /*
    try {
        dbRouter.doRouter();
        transactionTemplate.execute(status -> {
            try {
                return 1;
            } catch (DuplicateKeyException e) {
                status.setRollbackOnly();
                log.error("[更新用户中奖记录] 唯一索引冲突");
                throw new AppException(Constants.ExceptionInfo.DUPLICATE_KEY);
            }
        });
    } finally {
        dbRouter.clear();
    }
    */

}
