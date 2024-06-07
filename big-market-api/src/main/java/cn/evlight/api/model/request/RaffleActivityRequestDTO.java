package cn.evlight.api.model.request;

import lombok.Data;

/**
 * @Description: 活动抽奖请求参数
 * @Author: evlight
 * @Date: 2024/6/7
 */

@Data
public class RaffleActivityRequestDTO {
    private Long activityId;
    private String userId;
}
