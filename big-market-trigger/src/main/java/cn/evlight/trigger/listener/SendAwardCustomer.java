package cn.evlight.trigger.listener;

import cn.evlight.domain.award.event.SendAwardMessageEvent;
import cn.evlight.domain.award.model.entity.DistributeAwardEntity;
import cn.evlight.domain.award.service.IAwardService;
import cn.evlight.types.event.BaseEvent;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
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
 * @description 用户奖品记录消息消费者
 * @create 2024-04-06 12:09
 */
@Slf4j
@Component
public class SendAwardCustomer {

    @Value("${spring.rabbitmq.topic.send_award}")
    private String topic;

    @Autowired
    private IAwardService awardService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("send_award"),
            exchange = @Exchange(value = "send_award")
    ))
    public void listener(String message) {
        log.info("[MQ]-[consumer]-[用户奖品发放] topic: {} message: {}", topic, message);
        try {

            BaseEvent.EventMessage<SendAwardMessageEvent.Message> eventMessage = JSON.parseObject(message,
                    new TypeReference<BaseEvent.EventMessage<SendAwardMessageEvent.Message>>() {
            }.getType());
            SendAwardMessageEvent.Message sendAwardMessage = eventMessage.getData();
            DistributeAwardEntity distributeAwardEntity = DistributeAwardEntity.builder()
                        .userId(sendAwardMessage.getUserId())
                        .orderId(sendAwardMessage.getOrderId())
                        .awardId(sendAwardMessage.getAwardId())
                        .awardConfig(sendAwardMessage.getAwardConfig())
                        .build();
            awardService.distributeAward(distributeAwardEntity);
        } catch (Exception e) {
            log.error("[MQ]-[consumer]-[用户奖品发放]，消费失败 topic: {} message: {}", topic, message);
            throw e;
        }
    }

}
