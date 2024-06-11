package cn.evlight.domain.award.service;

import cn.evlight.domain.award.event.SendAwardMessageEvent;
import cn.evlight.domain.award.model.aggregate.UserAwardRecordAggregate;
import cn.evlight.domain.award.model.entity.DistributeAwardEntity;
import cn.evlight.domain.award.model.entity.TaskEntity;
import cn.evlight.domain.award.model.entity.UserAwardRecordEntity;
import cn.evlight.domain.award.model.valobj.TaskStateVO;
import cn.evlight.domain.award.repository.IAwardRepository;
import cn.evlight.domain.award.service.distribute.IDistributeAward;
import cn.evlight.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Description: 奖品服务实现类
 * @Author: evlight
 * @Date: 2024/6/7
 */

@Slf4j
@Service
public class AwardService implements IAwardService{

    @Autowired
    private IAwardRepository awardRepository;

    @Autowired
    private SendAwardMessageEvent sendAwardMessageEvent;

    @Autowired
    private Map<String, IDistributeAward> distributeAwardMap;

    @Override
    public void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity) {
        //构建消息对象
        BaseEvent.EventMessage<SendAwardMessageEvent.Message> eventMessage = sendAwardMessageEvent.buildEventMessage(SendAwardMessageEvent.Message.builder()
                .userId(userAwardRecordEntity.getUserId())
                .awardId(userAwardRecordEntity.getAwardId())
                .orderId(userAwardRecordEntity.getOrderId())
                .awardTitle(userAwardRecordEntity.getAwardTitle())
                .awardConfig(userAwardRecordEntity.getAwardConfig())
                .build());
        //构建消息发送任务对象
        TaskEntity taskEntity = TaskEntity.builder()
                .userId(userAwardRecordEntity.getUserId())
                .topic(sendAwardMessageEvent.topic())
                .messageId(eventMessage.getId())
                .message(eventMessage)
                .state(TaskStateVO.create)
                .build();
        //构建聚合对象
        UserAwardRecordAggregate userAwardRecordAggregate = UserAwardRecordAggregate.builder()
                .userAwardRecordEntity(userAwardRecordEntity)
                .taskEntity(taskEntity)
                .build();
        //保存聚合对象
        awardRepository.saveUserAwardRecord(userAwardRecordAggregate);
    }

    @Override
    public void distributeAward(DistributeAwardEntity distributeAwardEntity) {
        String awardKey = awardRepository.getAwardKey(distributeAwardEntity.getAwardId());
        if (awardKey == null){
            log.error("奖品key不存在:{}", distributeAwardEntity.getAwardId());
        }
        IDistributeAward distributeAwardService = distributeAwardMap.get(awardKey);
        if(distributeAwardService == null){
            log.error("奖品发放实现类不存在:{}", awardKey);
            return;
        }
        //发放奖品
        distributeAwardService.distributeAward(distributeAwardEntity);
    }

}
