package cn.evlight.trigger.listener;

import cn.evlight.domain.activity.model.entity.RaffleActivityQuotaEntity;
import cn.evlight.domain.activity.service.IRaffleActivityQuota;
import cn.evlight.domain.rebate.event.SendRebateEventMessage;
import cn.evlight.domain.rebate.model.valobj.RebateTypeVO;
import cn.evlight.types.event.BaseEvent;
import cn.evlight.types.exception.AppException;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description: 行为返利消息消费者
 * @Author: evlight
 * @Date: 2024/6/9
 */
@Slf4j
@Component
public class BehaviorRebateConsumer {

    @Autowired
    private IRaffleActivityQuota raffleActivityQuota;

    @Value("${spring.rabbitmq.topic.send_rebate}")
    private String topic;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("send_rebate"),
            exchange = @Exchange(value = "send_rebate")
    ))
    public void listener(String message) {
        log.info("[MQ]-[行为返利] topic: {} message: {}", topic, message);
        try {
            BaseEvent.EventMessage<SendRebateEventMessage.Message> eventMessage = JSON.parseObject(message,
                    new TypeReference<BaseEvent.EventMessage<SendRebateEventMessage.Message>>(){}.getType());
            SendRebateEventMessage.Message rebateMessage = eventMessage.getData();
            if (!rebateMessage.getRebateType().equals(RebateTypeVO.SKU.getCode())){
                //非sku返利不需要处理
                return;
            }
            raffleActivityQuota.createQuotaOrder(RaffleActivityQuotaEntity.builder()
                    .userId(rebateMessage.getUserId())
                    .sku(Long.parseLong(rebateMessage.getRebateConfig()))
                    .outBizId(rebateMessage.getBizId())
                    .build());
        }catch (AppException e){
            log.info("[MQ]-[行为返利] 消费失败 原因:{}", e.getCode());
            throw e;
        }catch (Exception e){
            log.info("[MQ]-[行为返利] 消费失败 原因:未知错误");
        }
    }

}
