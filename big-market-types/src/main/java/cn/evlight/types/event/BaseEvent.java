package cn.evlight.types.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 基础时间
 * @create 2024-03-30 12:42
 */
@Data
public abstract class BaseEvent<T> {

    public abstract EventMessage<T> buildEventMessage(T data);

    public abstract String topic();

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EventMessage<T> {
        private String id; //消息id
        private LocalDateTime timestamp; //消息生成时间
        private T data; //消息体
    }

}
