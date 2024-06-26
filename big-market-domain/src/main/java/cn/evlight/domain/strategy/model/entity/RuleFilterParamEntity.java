package cn.evlight.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description: 规则过滤参数
 * @Author: evlight
 * @Date: 2024/5/25
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleFilterParamEntity {

    private String userId;
    private Long strategyId;
    private Integer awardId;
    private String ruleValue;
    private LocalDateTime endDateTime;

}
