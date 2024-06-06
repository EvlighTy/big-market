package cn.evlight.trigger.listener;

import cn.evlight.domain.activity.service.IRaffleActivitySkuStock;
import cn.evlight.types.event.BaseEvent;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 活动sku库存耗尽
 * @create 2024-03-30 12:31
 */
@Slf4j
@Component
public class ActivitySkuStockZeroCustomer {

    @Value("${spring.rabbitmq.topic.activity_sku_stock_zero}")
    private String topic;

    @Autowired
    private IRaffleActivitySkuStock skuStock;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("activity_sku_stock_zero"),
            exchange = @Exchange(value = "activity_sku_stock_zero", delayed = "true")
    ))
    public void listener(String message) {
        try {
            log.info("监听到sku库存消耗为0消息 topic: {} message: {}", topic, message);
            BaseEvent.EventMessage<Long> eventMessage = JSON.parseObject(message,
                    new TypeReference<BaseEvent.EventMessage<Long>>() {}.getType());
            Long sku = eventMessage.getData();
            //清空库存
            skuStock.clearActivitySkuStock(sku);
            //清空延迟更新队列
            skuStock.clearQueueValue();
        } catch (Exception e) {
            log.error("监听sku库存消耗为0消息但消费失败 topic: {} message: {}", topic, message);
            throw e;
        }
    }

}
