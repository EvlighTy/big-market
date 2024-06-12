package cn.evlight.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.evlight.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import cn.evlight.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import cn.evlight.domain.rebate.model.entity.TaskEntity;
import cn.evlight.domain.rebate.model.valobj.BehaviorTypeVO;
import cn.evlight.domain.rebate.model.valobj.DailyBehaviorRebateVO;
import cn.evlight.domain.rebate.repository.IBehaviorRebateRepository;
import cn.evlight.infrastructure.event.EventPublisher;
import cn.evlight.infrastructure.persistent.dao.DailyBehaviorRebateMapper;
import cn.evlight.infrastructure.persistent.dao.TaskMapper;
import cn.evlight.infrastructure.persistent.dao.UserBehaviorRebateOrderMapper;
import cn.evlight.infrastructure.persistent.po.DailyBehaviorRebate;
import cn.evlight.infrastructure.persistent.po.Task;
import cn.evlight.infrastructure.persistent.po.UserBehaviorRebateOrder;
import cn.evlight.types.common.Constants;
import cn.evlight.types.exception.AppException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description: 行为返利仓库实现类
 * @Author: evlight
 * @Date: 2024/6/9
 */

@Slf4j
@Repository
public class BehaviorRebateRepository implements IBehaviorRebateRepository {

    @Autowired
    private DailyBehaviorRebateMapper dailyBehaviorRebateMapper;

    @Autowired
    private UserBehaviorRebateOrderMapper userBehaviorRebateOrderMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private IDBRouterStrategy dbRouter;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private EventPublisher eventPublisher;

    @Override
    public List<DailyBehaviorRebateVO> getDailyBehaviorRebateConfig(BehaviorTypeVO behaviorTypeVO) {
        DailyBehaviorRebate dailyBehaviorRebate = new DailyBehaviorRebate();
        dailyBehaviorRebate.setBehaviorType(behaviorTypeVO.getCode());
        List<DailyBehaviorRebate> dailyBehaviorRebates = dailyBehaviorRebateMapper.getDailyBehaviorRebateConfig(dailyBehaviorRebate);
        ArrayList<DailyBehaviorRebateVO> dailyBehaviorRebateVOS = new ArrayList<>(dailyBehaviorRebates.size());
        for (DailyBehaviorRebate behaviorRebate : dailyBehaviorRebates) {
            DailyBehaviorRebateVO dailyBehaviorRebateVO = DailyBehaviorRebateVO.builder()
                    .behaviorType(behaviorRebate.getBehaviorType())
                    .rebateDesc(behaviorRebate.getRebateDesc())
                    .rebateType(behaviorRebate.getRebateType())
                    .rebateConfig(behaviorRebate.getRebateConfig())
                    .build();
            dailyBehaviorRebateVOS.add(dailyBehaviorRebateVO);
        }
        return dailyBehaviorRebateVOS;
    }

    @Override
    public void saveUserBehaviorRebateOrder(ArrayList<BehaviorRebateAggregate> behaviorRebateAggregates) {
        AtomicBoolean success = new AtomicBoolean(false);
        try {
            dbRouter.doRouter(behaviorRebateAggregates.get(0).getUserId());
            transactionTemplate.execute(status -> {
                try {
                    ArrayList<UserBehaviorRebateOrder> userBehaviorRebateOrders = new ArrayList<>(behaviorRebateAggregates.size());
                    ArrayList<Task> tasks = new ArrayList<>();
                    for (BehaviorRebateAggregate behaviorRebateAggregate : behaviorRebateAggregates) {
                        //订单对象
                        UserBehaviorRebateOrderEntity behaviorRebateOrderEntity = behaviorRebateAggregate.getBehaviorRebateOrderEntity();
                        UserBehaviorRebateOrder userBehaviorRebateOrder = new UserBehaviorRebateOrder();
                        userBehaviorRebateOrder.setUserId(behaviorRebateOrderEntity.getUserId());
                        userBehaviorRebateOrder.setOrderId(behaviorRebateOrderEntity.getOrderId());
                        userBehaviorRebateOrder.setBehaviorType(behaviorRebateOrderEntity.getBehaviorType());
                        userBehaviorRebateOrder.setRebateDesc(behaviorRebateOrderEntity.getRebateDesc());
                        userBehaviorRebateOrder.setRebateType(behaviorRebateOrderEntity.getRebateType());
                        userBehaviorRebateOrder.setRebateConfig(behaviorRebateOrderEntity.getRebateConfig());
                        userBehaviorRebateOrder.setOutBusinessNo(behaviorRebateOrderEntity.getOutBusinessNo());
                        userBehaviorRebateOrder.setBizId(behaviorRebateOrderEntity.getBizId());
                        userBehaviorRebateOrders.add(userBehaviorRebateOrder);
                        //任务对象
                        TaskEntity taskEntity = behaviorRebateAggregate.getTaskEntity();
                        Task task = new Task();
                        task.setUserId(taskEntity.getUserId());
                        task.setTopic(taskEntity.getTopic());
                        task.setMessageId(taskEntity.getMessageId());
                        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
                        task.setState(taskEntity.getState().getCode());
                        tasks.add(task);
                    }
                    //批量插入
                    userBehaviorRebateOrderMapper.saveBatch(userBehaviorRebateOrders);
                    taskMapper.saveBatch(tasks);
                    success.set(true);
                    return behaviorRebateAggregates.size();
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    throw new AppException(Constants.ExceptionInfo.DUPLICATE_KEY);
                }catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            });
        } finally {
            dbRouter.clear();
        }
        //发送消息到MQ
        if(!success.get()){
            return;
        }
        ArrayList<Task> successSendTasks = new ArrayList<>(behaviorRebateAggregates.size());
        ArrayList<Task> failSendTasks = new ArrayList<>(behaviorRebateAggregates.size());
        for (BehaviorRebateAggregate behaviorRebateAggregate : behaviorRebateAggregates) {
            TaskEntity taskEntity = behaviorRebateAggregate.getTaskEntity();
            Task task = new Task();
            task.setUserId(behaviorRebateAggregate.getUserId());
            task.setMessageId(taskEntity.getMessageId());
            try {
                eventPublisher.publish(taskEntity.getTopic(), taskEntity.getMessage());
                successSendTasks.add(task);
            }catch (Exception e){
                log.info("[MQ]-[保存行为返利订单记录]-[publisher] 发送MQ消息失败");
                failSendTasks.add(task);
            }
        }
        if(!successSendTasks.isEmpty()){
            taskMapper.updateBatchAfterCompleted(successSendTasks);
        }
        if(!failSendTasks.isEmpty()){
            taskMapper.updateBatchAfterFailed(failSendTasks);
        }
    }

    @Override
    public List<UserBehaviorRebateOrderEntity> getUserBehaviorRebateOrderEntityByOutBizId(String userId, String outBizId) {
        UserBehaviorRebateOrder userBehaviorRebateOrder = new UserBehaviorRebateOrder();
        userBehaviorRebateOrder.setUserId(userId);
        userBehaviorRebateOrder.setOutBusinessNo(outBizId);
        List<UserBehaviorRebateOrder> userBehaviorRebateOrders = userBehaviorRebateOrderMapper.getUserBehaviorRebateOrderByOutBizId(userBehaviorRebateOrder);
        if(userBehaviorRebateOrders == null || userBehaviorRebateOrders.isEmpty()){
            //没有记录
            return Collections.emptyList();
        }
        ArrayList<UserBehaviorRebateOrderEntity> userBehaviorRebateOrderEntities = new ArrayList<>(userBehaviorRebateOrders.size());
        for (UserBehaviorRebateOrder behaviorRebateOrder : userBehaviorRebateOrders) {
            UserBehaviorRebateOrderEntity userBehaviorRebateOrderEntity = UserBehaviorRebateOrderEntity.builder()
                        .userId(behaviorRebateOrder.getUserId())
                        .orderId(behaviorRebateOrder.getOrderId())
                        .behaviorType(behaviorRebateOrder.getBehaviorType())
                        .rebateDesc(behaviorRebateOrder.getRebateDesc())
                        .rebateType(behaviorRebateOrder.getRebateType())
                        .rebateConfig(behaviorRebateOrder.getRebateConfig())
                        .bizId(behaviorRebateOrder.getBizId())
                        .outBusinessNo(behaviorRebateOrder.getOutBusinessNo())
                        .build();
            userBehaviorRebateOrderEntities.add(userBehaviorRebateOrderEntity);
        }
        return userBehaviorRebateOrderEntities;
    }

}
