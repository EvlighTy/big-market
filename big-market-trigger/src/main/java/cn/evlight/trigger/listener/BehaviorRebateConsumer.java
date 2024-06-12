package cn.evlight.trigger.listener;

import cn.evlight.domain.activity.model.entity.RaffleActivityQuotaEntity;
import cn.evlight.domain.activity.model.valobj.OrderTradeTypeVO;
import cn.evlight.domain.activity.service.IRaffleActivityQuota;
import cn.evlight.domain.credit.model.entity.CreditEntity;
import cn.evlight.domain.credit.model.valobj.TradeNameVO;
import cn.evlight.domain.credit.model.valobj.TradeTypeVO;
import cn.evlight.domain.credit.service.ICreditService;
import cn.evlight.domain.rebate.event.SendRebateEventMessage;
import cn.evlight.types.event.BaseEvent;
import cn.evlight.types.exception.AppException;
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

import java.math.BigDecimal;

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

    @Autowired
    private ICreditService creditService;

    @Value("${spring.rabbitmq.topic.send_rebate}")
    private String topic;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("send_rebate"),
            exchange = @Exchange(value = "send_rebate")
    ))
    public void listener(String message) {
        log.info("[MQ]-[consumer]-[行为返利] topic: {} message: {}", topic, message);
        try {
            BaseEvent.EventMessage<SendRebateEventMessage.Message> eventMessage = JSON.parseObject(message,
                    new TypeReference<BaseEvent.EventMessage<SendRebateEventMessage.Message>>(){}.getType());
            SendRebateEventMessage.Message rebateMessage = eventMessage.getData();
            String rebateType = rebateMessage.getRebateType();
            switch (rebateType){
                case "sku":
                    raffleActivityQuota.createQuotaOrder(RaffleActivityQuotaEntity.builder()
                            .userId(rebateMessage.getUserId())
                            .sku(Long.parseLong(rebateMessage.getRebateConfig()))
                            .orderTradeTypeVO(OrderTradeTypeVO.rebate_no_pay_trade)
                            .outBizId(rebateMessage.getBizId())
                            .build());
                    break;
                case "integral":
                    creditService.createOrder(CreditEntity.builder()
                            .userId(rebateMessage.getUserId())
                            .tradeName(TradeNameVO.REBATE)
                            .tradeType(TradeTypeVO.FORWARD)
                            .amount(new BigDecimal(rebateMessage.getRebateConfig()))
                            .outBusinessNo(rebateMessage.getBizId())
                            .build());
                    break;
            }
        }catch (AppException e){
            log.info("[MQ]-[consumer]-[行为返利] 消费失败 原因:{}", e.getCode());
            throw e;
        }catch (Exception e){
            log.info("[MQ]-[consumer]-[行为返利] 消费失败 原因:未知错误");
        }
    }

}
