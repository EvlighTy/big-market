package cn.evlight.api.model.response;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 抽奖响应结果
 * @Author: evlight
 * @Date: 2024/5/29
 */

@Builder
@Data
public class RaffleStrategyResponseDTO {
    private Integer awardId; //奖品ID
    private Integer awardIndex; //奖品排序编号
}
