package cn.evlight.domain.award.service;

import cn.evlight.domain.award.event.SendAwardMessageEvent;
import cn.evlight.domain.award.model.aggregate.UserAwardRecordAggregate;
import cn.evlight.domain.award.model.entity.TaskEntity;
import cn.evlight.domain.award.model.entity.UserAwardRecordEntity;
import cn.evlight.domain.award.model.valobj.TaskStateVO;
import cn.evlight.domain.award.repository.IAwardRepository;
import cn.evlight.types.event.BaseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: 奖品发放服务
 * @Author: evlight
 * @Date: 2024/6/7
 */

@Service
public class AwardService implements IAwardService{

    @Autowired
    private IAwardRepository awardRepository;

    @Autowired
    private SendAwardMessageEvent sendAwardMessageEvent;

    @Override
    public void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity) {
        //构建消息对象
        BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> sendAwardMessageEventMessage = sendAwardMessageEvent.buildEventMessage(SendAwardMessageEvent.SendAwardMessage.builder()
                .userId(userAwardRecordEntity.getUserId())
                .awardId(userAwardRecordEntity.getAwardId())
                .awardTitle(userAwardRecordEntity.getAwardTitle())
                .build());
        //构建消息发送任务对象
        TaskEntity taskEntity = TaskEntity.builder()
                .userId(userAwardRecordEntity.getUserId())
                .topic(sendAwardMessageEvent.topic())
                .messageId(sendAwardMessageEventMessage.getId())
                .message(sendAwardMessageEventMessage)
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

}
