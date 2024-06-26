package cn.evlight.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 抽奖结果实体
 * @Author: evlight
 * @Date: 2024/5/25
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleResultEntity {

    private Integer awardId;
    private String awardTitle;
    private String awardConfig;
    private Integer sort;
}
