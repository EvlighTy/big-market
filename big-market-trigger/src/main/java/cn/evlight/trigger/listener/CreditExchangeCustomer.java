package cn.evlight.trigger.listener;

import cn.evlight.domain.activity.model.entity.CreditExchangeEntity;
import cn.evlight.domain.activity.service.IRaffleActivityQuota;
import cn.evlight.domain.credit.model.event.CreditExchangeMessageEvent;
import cn.evlight.types.event.BaseEvent;
import cn.evlight.types.exception.AppException;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 积分调整成功消息
 * @create 2024-06-08 19:38
 */
@Slf4j
@Component
public class CreditExchangeCustomer {

    @Value("${spring.rabbitmq.topic.credit_exchange}")
    private String topic;
    @Resource
    private IRaffleActivityQuota raffleActivityQuota;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("credit_exchange"),
            exchange = @Exchange(value = "credit_exchange")
    ))
    public void listener(String message) {
        try {
            log.info("[MQ]-[consumer]-[用户积分兑换] topic: {} message: {}", topic, message);
            BaseEvent.EventMessage<CreditExchangeMessageEvent.Message> eventMessage = JSON.parseObject(message,
                    new TypeReference<BaseEvent.EventMessage<CreditExchangeMessageEvent.Message>>() {
            }.getType());
            CreditExchangeMessageEvent.Message eventMessageData = eventMessage.getData();
            //积分兑换
            CreditExchangeEntity creditExchangeEntity = CreditExchangeEntity.builder()
                        .userId(eventMessageData.getUserId())
                        .outBusinessNo(eventMessageData.getOutBusinessNo())
                        .build();
            raffleActivityQuota.creditExchange(creditExchangeEntity);
        } catch (AppException e) {
            log.warn("[MQ]-[consumer]-[用户积分兑换] 消费重复 topic: {} message: {}", topic, message, e);
        } catch (Exception e) {
            log.error("[MQ]-[consumer]-[用户积分兑换] 消费失败 topic: {} message: {}", topic, message, e);
            throw e;
        }
    }

}
