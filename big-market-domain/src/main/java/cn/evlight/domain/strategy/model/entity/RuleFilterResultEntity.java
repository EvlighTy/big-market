package cn.evlight.domain.strategy.model.entity;

import cn.evlight.domain.strategy.model.valobj.RuleFilterStateVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 规则过滤结果实体
 * @Author: evlight
 * @Date: 2024/5/25
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleFilterResultEntity <T extends RuleFilterResultEntity.RuleFilterResult> {

    private String stateCode = RuleFilterStateVO.ALLOW.getCode();
    private String stateInfo = RuleFilterStateVO.ALLOW.getInfo();
    private String ruleModel;
    private T result;

    public static class RuleFilterResult {

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BeforeRaffleRuleResult extends RuleFilterResult{
        /**
         * 策略ID
         */
        private Long strategyId;

        /**
         * 权重值Key；用于抽奖时可以选择权重抽奖。
         */
        private String ruleWeightValueKey;

        /**
         * 奖品ID；
         */
        private Integer awardId;
    }

    public static class AfterRaffleRuleResult extends RuleFilterResult{

    }

}
