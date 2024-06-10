package cn.evlight.domain.rebate.service;

import cn.evlight.domain.award.model.valobj.TaskStateVO;
import cn.evlight.domain.rebate.event.SendRebateEventMessage;
import cn.evlight.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import cn.evlight.domain.rebate.model.entity.BehaviorEntity;
import cn.evlight.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import cn.evlight.domain.rebate.model.entity.TaskEntity;
import cn.evlight.domain.rebate.model.valobj.DailyBehaviorRebateVO;
import cn.evlight.domain.rebate.repository.IBehaviorRebateRepository;
import cn.evlight.types.common.Constants;
import cn.evlight.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 行为服务实现类
 * @Author: evlight
 * @Date: 2024/6/9
 */
@Slf4j
@Service
public class BehaviorRebateService implements IBehaviorRebateService{

    @Autowired
    private IBehaviorRebateRepository behaviorRebateRepository;

    @Autowired
    private SendRebateEventMessage sendRebateEventMessage;

    @Override
    public List<String> createOrder(BehaviorEntity behaviorEntity) {
        log.info("保存行为返利订单...");
        //查询返利配置
        List<DailyBehaviorRebateVO> dailyBehaviorRebateVOS = behaviorRebateRepository.getDailyBehaviorRebateConfig(behaviorEntity.getBehaviorTypeVO());
        //构建聚合对象
        ArrayList<BehaviorRebateAggregate> behaviorRebateAggregates = new ArrayList<>(dailyBehaviorRebateVOS.size());
        //订单id集合
        ArrayList<String> orderIds = new ArrayList<>(dailyBehaviorRebateVOS.size());
        for (DailyBehaviorRebateVO dailyBehaviorRebateVO : dailyBehaviorRebateVOS) {
            //订单对象
            //业务id
            String bizId = behaviorEntity.getUserId() + Constants.Split.UNDERLINE +
                    dailyBehaviorRebateVO.getRebateType() + Constants.Split.UNDERLINE +
                    behaviorEntity.getOutBizId();
            UserBehaviorRebateOrderEntity behaviorRebateOrderEntity = UserBehaviorRebateOrderEntity.builder()
                        .userId(behaviorEntity.getUserId())
                        .orderId(RandomStringUtils.randomNumeric(12))
                        .behaviorType(dailyBehaviorRebateVO.getBehaviorType())
                        .rebateDesc(dailyBehaviorRebateVO.getRebateDesc())
                        .rebateType(dailyBehaviorRebateVO.getRebateType())
                        .rebateConfig(dailyBehaviorRebateVO.getRebateConfig())
                        .outBusinessNo(behaviorEntity.getOutBizId())
                        .bizId(bizId)
                        .build();
            //订单id
            orderIds.add(behaviorRebateOrderEntity.getOrderId());
            //消息对象
            BaseEvent.EventMessage<SendRebateEventMessage.Message> eventMessage = sendRebateEventMessage.buildEventMessage(SendRebateEventMessage.Message.builder()
                    .userId(behaviorEntity.getUserId())
                    .bizId(bizId)
                    .rebateDesc(dailyBehaviorRebateVO.getRebateDesc())
                    .rebateConfig(dailyBehaviorRebateVO.getRebateConfig())
                    .rebateType(dailyBehaviorRebateVO.getRebateType())
                    .build());
            //任务实体
            TaskEntity taskEntity = TaskEntity.builder()
                    .userId(behaviorEntity.getUserId())
                    .topic(sendRebateEventMessage.topic())
                    .messageId(eventMessage.getId())
                    .message(eventMessage)
                    .state(TaskStateVO.create)
                    .build();
            //组装聚合对象
            behaviorRebateAggregates.add(BehaviorRebateAggregate.builder()
                    .userId(behaviorEntity.getUserId())
                    .behaviorRebateOrderEntity(behaviorRebateOrderEntity)
                    .taskEntity(taskEntity)
                    .build());
        }
        behaviorRebateRepository.saveUserBehaviorRebateOrder(behaviorRebateAggregates);
        return orderIds;
    }

    @Override
    public List<UserBehaviorRebateOrderEntity> getUserBehaviorRebateOrderEntityByOutBizId(String userId, String outBizId) {
        return behaviorRebateRepository.getUserBehaviorRebateOrderEntityByOutBizId(userId, outBizId);
    }

}
