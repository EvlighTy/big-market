package cn.evlight.domain.activity.event;

import cn.evlight.types.event.BaseEvent;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 活动sku库存清空消息
 * @create 2024-03-30 12:43
 */
@Component
public class ActivitySkuStockZeroMessageEvent extends BaseEvent<Long> {

    @Value("${spring.rabbitmq.topic.activity_sku_stock_zero}")
    private String topic;

    @Override
    public BaseEvent.EventMessage<Long> buildEventMessage(Long sku) {
        return EventMessage.<Long>builder()
                .id(RandomStringUtils.randomNumeric(11))
                .timestamp(LocalDateTime.now())
                .data(sku)
                .build();
    }

    @Override
    public String topic() {
        return topic;
    }

}
