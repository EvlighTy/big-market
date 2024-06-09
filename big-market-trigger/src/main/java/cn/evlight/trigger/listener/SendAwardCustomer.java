package cn.evlight.trigger.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 用户奖品记录消息消费者
 * @create 2024-04-06 12:09
 */
@Slf4j
@Component
public class SendAwardCustomer {

    @Value("${spring.rabbitmq.topic.send_award}")
    private String topic;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("send_award"),
            exchange = @Exchange(value = "send_award")
    ))
    public void listener(String message) {
        try {
            log.info("[MQ]-[用户奖品发放] topic: {} message: {}", topic, message);
        } catch (Exception e) {
            log.error("[MQ]-[用户奖品发放]，消费失败 topic: {} message: {}", topic, message);
            throw e;
        }
    }

}
