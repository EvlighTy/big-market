package cn.evlight.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.evlight.domain.award.model.aggregate.UserAwardRecordAggregate;
import cn.evlight.domain.award.model.entity.TaskEntity;
import cn.evlight.domain.award.model.entity.UserAwardRecordEntity;
import cn.evlight.domain.award.repository.IAwardRepository;
import cn.evlight.infrastructure.event.EventPublisher;
import cn.evlight.infrastructure.persistent.dao.TaskMapper;
import cn.evlight.infrastructure.persistent.dao.UserAwardRecordMapper;
import cn.evlight.infrastructure.persistent.dao.UserRaffleOrderMapper;
import cn.evlight.infrastructure.persistent.po.Task;
import cn.evlight.infrastructure.persistent.po.UserAwardRecord;
import cn.evlight.infrastructure.persistent.po.UserRaffleOrder;
import cn.evlight.types.common.Constants;
import cn.evlight.types.exception.AppException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

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
            eventPublisher.publish(task.getTopic(), task.getMessage());
            //发送成功更新任务状态
            log.info("[保存用户中奖记录] 发送MQ消息成功 userId: {} topic: {}", userAwardRecord.getUserId(), task.getTopic());
            taskDao.updateAfterCompleted(task);
        } catch (Exception e) {
            //发送失败更新任务状态
            log.error("[保存用户中奖记录] 发送MQ消息失败 userId: {} topic: {}", userAwardRecord.getUserId(), task.getTopic());
            taskDao.updateAfterFailed(task);
        }
    }
}
