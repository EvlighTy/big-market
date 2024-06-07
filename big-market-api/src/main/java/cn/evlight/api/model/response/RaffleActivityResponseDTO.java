package cn.evlight.api.model.response;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 活动抽奖响应结果
 * @Author: evlight
 * @Date: 2024/6/7
 */

@Builder
@Data
public class RaffleActivityResponseDTO {
    private Integer awardId;
    private String awardTitle;
    private Integer awardIndex;
}
