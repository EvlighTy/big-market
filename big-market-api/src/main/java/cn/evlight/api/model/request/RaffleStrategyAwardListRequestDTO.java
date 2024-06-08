package cn.evlight.api.model.request;

import lombok.Data;

/**
 * @Description: 策略奖品列表请求参数
 * @Author: evlight
 * @Date: 2024/5/29
 */

@Data
public class RaffleStrategyAwardListRequestDTO {
    @Deprecated
    private Long strategyId; //策略ID
    private Long activityId; //活动ID
    private String userId; //用户ID
}
