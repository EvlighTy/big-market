package cn.evlight.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.evlight.domain.credit.model.aggregate.CreditAggregate;
import cn.evlight.domain.credit.model.entity.TaskEntity;
import cn.evlight.domain.credit.model.entity.UserCreditAccountEntity;
import cn.evlight.domain.credit.model.entity.UserCreditOrderEntity;
import cn.evlight.domain.credit.model.valobj.AccountStatusVO;
import cn.evlight.domain.credit.repository.ICreditRepository;
import cn.evlight.infrastructure.event.EventPublisher;
import cn.evlight.infrastructure.persistent.dao.TaskMapper;
import cn.evlight.infrastructure.persistent.dao.UserCreditAccountMapper;
import cn.evlight.infrastructure.persistent.dao.UserCreditOrderMapper;
import cn.evlight.infrastructure.persistent.po.Task;
import cn.evlight.infrastructure.persistent.po.UserCreditAccount;
import cn.evlight.infrastructure.persistent.po.UserCreditOrder;
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

import java.util.concurrent.TimeUnit;

/**
 * @Description: 积分仓库实现类
 * @Author: evlight
 * @Date: 2024/6/11
 */
@Slf4j
@Repository
public class CreditRepository implements ICreditRepository {

    @Autowired
    private IRedisService redisService;

    @Autowired
    private UserCreditAccountMapper userCreditAccountMapper;

    @Autowired
    private UserCreditOrderMapper userCreditOrderMapper;

    @Autowired
    private IDBRouterStrategy dbRouter;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private EventPublisher eventPublisher;

    @Override
    public void saveCreditAggregate(CreditAggregate creditAggregate) {
        UserCreditAccountEntity userCreditAccountEntity = creditAggregate.getUserCreditAccountEntity();
        UserCreditOrderEntity userCreditOrderEntity = creditAggregate.getUserCreditOrderEntity();
        TaskEntity taskEntity = creditAggregate.getTaskEntity();
        //积分账户对象
        UserCreditAccount userCreditAccount = UserCreditAccount.builder()
                .userId(userCreditAccountEntity.getUserId())
                .totalAmount(userCreditAccountEntity.getAdjustAmount())
                .availableAmount(userCreditAccountEntity.getAdjustAmount())
                .accountStatus(AccountStatusVO.open.getCode())
                .build();
        //积分订单对象
        UserCreditOrder userCreditOrder = UserCreditOrder.builder()
                .userId(userCreditOrderEntity.getUserId())
                .orderId(userCreditOrderEntity.getOrderId())
                .tradeName(userCreditOrderEntity.getTradeName().getName())
                .tradeType(userCreditOrderEntity.getTradeType().getCode())
                .tradeAmount(userCreditOrderEntity.getTradeAmount())
                .outBusinessNo(userCreditOrderEntity.getOutBusinessNo())
                .build();
        //任务对象
        Task task = Task.builder()
                .userId(taskEntity.getUserId())
                .topic(taskEntity.getTopic())
                .messageId(taskEntity.getMessageId())
                .message(JSON.toJSONString(taskEntity.getMessage()))
                .state(taskEntity.getState().getCode())
                .build();
        String lockKey = Constants.RedisKey.CREDIT_ACCOUNT_LOCK + creditAggregate.getUserId() + Constants.Split.UNDERLINE + userCreditOrder.getOutBusinessNo();
        RLock lock = redisService.getLock(lockKey);
        try {
            lock.lock(3, TimeUnit.SECONDS);
            dbRouter.doRouter(creditAggregate.getUserId());
            transactionTemplate.execute(status -> {
                try {
                    //更新账户积分值
                    int updated = userCreditAccountMapper.addAmount(userCreditAccount);
                    if(updated != 1){
                        //账户不存在 创建
                        userCreditAccountMapper.save(userCreditAccount);
                    }
                    //保存积分订单
                    userCreditOrderMapper.save(userCreditOrder);
                    //保存积分兑换任务
                    taskMapper.save(task);
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("[repository]-[更新用户积分账户] 失败:唯一索引冲突");
                    throw new AppException(Constants.ExceptionInfo.DUPLICATE_KEY);
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.error("[repository]-[更新用户积分账户] 失败:未知错误");
                }
                return 1;
            });
        } finally {
            dbRouter.clear();
            lock.unlock();
        }
        //发送消息到MQ
        try {
            eventPublisher.publish(taskEntity.getTopic(), taskEntity.getMessage());
            taskMapper.updateAfterCompleted(task);
            log.error("[MQ]-[publisher]-[用户积分消耗] 消息发送成功");
        } catch (Exception e) {
            log.error("[MQ]-[publisher]-[用户积分消耗] 消息发送失败");
        }
    }

    /*
    try {
        dbRouter.doRouter();
        transactionTemplate.execute(status -> {
            try {

            } catch (DuplicateKeyException e) {
                status.setRollbackOnly();
                log.error("[repository]-[] 失败:唯一索引冲突");
                throw new AppException(Constants.ExceptionInfo.DUPLICATE_KEY);
            } catch (Exception e) {
                    status.setRollbackOnly();
                    log.error("[repository]-[] 失败:未知错误");
                }
            return 1;
        });
    } finally {
        dbRouter.clear();
    }
    */

}
