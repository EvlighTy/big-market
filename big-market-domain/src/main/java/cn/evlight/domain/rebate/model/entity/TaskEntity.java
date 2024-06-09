package cn.evlight.domain.rebate.model.entity;

import cn.evlight.domain.award.model.valobj.TaskStateVO;
import cn.evlight.domain.rebate.event.SendRebateEventMessage;
import cn.evlight.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 任务实体
 * @Author: evlight
 * @Date: 2024/6/9
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {

    /** 活动ID */
    private String userId;
    /** 消息主题 */
    private String topic;
    /** 消息编号 */
    private String messageId;
    /** 消息主体 */
    private BaseEvent.EventMessage<SendRebateEventMessage.Message> message;
    /** 任务状态；create-创建、completed-完成、fail-失败 */
    private TaskStateVO state;

}
