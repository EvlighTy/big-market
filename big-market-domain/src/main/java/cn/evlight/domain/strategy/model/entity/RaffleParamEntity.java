package cn.evlight.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 抽奖参数实体
 * @Author: evlight
 * @Date: 2024/5/25
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleParamEntity {

    private String userId;
    private Long strategyId;

}
