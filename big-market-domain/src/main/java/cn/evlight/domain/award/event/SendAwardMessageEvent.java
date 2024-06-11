package cn.evlight.domain.award.event;

import cn.evlight.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 用户奖品记录事件消息
 * @create 2024-04-06 09:43
 */
@Component
public class SendAwardMessageEvent extends BaseEvent<SendAwardMessageEvent.Message> {

    @Value("${spring.rabbitmq.topic.send_award}")
    private String topic;

    @Override
    public BaseEvent.EventMessage<Message> buildEventMessage(Message data) {
        return EventMessage.<Message>builder()
                .id(RandomStringUtils.randomNumeric(11))
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    @Override
    public String topic() {
        return topic;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        /**
         * 用户ID
         */
        private String userId;
        /**
         * 奖品ID
         */
        private Integer awardId;

        private String orderId;
        /**
         * 奖品标题（名称）
         */
        private String awardTitle;

        private String awardConfig;
    }

}
