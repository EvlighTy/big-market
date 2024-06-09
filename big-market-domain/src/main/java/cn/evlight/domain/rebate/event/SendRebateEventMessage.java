package cn.evlight.domain.rebate.event;

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
 * @Description: 发送返利事件消息
 * @Author: evlight
 * @Date: 2024/6/9
 */

@Component
public class SendRebateEventMessage extends BaseEvent<SendRebateEventMessage.Message>{

    @Value("${spring.rabbitmq.topic.send_rebate}")
    private String topic;

    @Override
    public BaseEvent.EventMessage<Message> buildEventMessage(Message data) {
        return BaseEvent.EventMessage.<SendRebateEventMessage.Message>builder()
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
        /** 用户ID */
        private String userId;
        /** 返利描述 */
        private String rebateDesc;
        /** 返利类型 */
        private String rebateType;
        /** 返利配置 */
        private String rebateConfig;
        /** 业务ID - 唯一ID，确保幂等 */
        private String bizId;
    }

}
