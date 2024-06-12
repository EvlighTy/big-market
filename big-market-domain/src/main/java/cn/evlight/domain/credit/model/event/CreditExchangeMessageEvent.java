package cn.evlight.domain.credit.model.event;

import cn.evlight.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Description: 积分兑换消息体
 * @Author: evlight
 * @Date: 2024/6/12
 */
@Component
public class CreditExchangeMessageEvent extends BaseEvent<CreditExchangeMessageEvent.Message>{

    @Value("${spring.rabbitmq.topic.credit_exchange}")
    private String topic;

    @Override
    public BaseEvent.EventMessage<Message> buildEventMessage(Message data) {
        return BaseEvent.EventMessage.<Message>builder()
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
         * 订单ID
         */
        private String orderId;
        /**
         * 交易金额
         */
        private BigDecimal amount;
        /**
         * 业务仿重ID - 外部透传。返利、行为等唯一标识
         */
        private String outBusinessNo;
    }

}
