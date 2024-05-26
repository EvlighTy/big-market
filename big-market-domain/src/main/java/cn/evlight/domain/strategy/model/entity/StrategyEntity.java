package cn.evlight.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 策略实体
 * @Author: evlight
 * @Date: 2024/5/25
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyEntity {

    /**
     * 抽奖策略ID
     */
    private Long strategyId;

    /**
     * 抽奖策略描述
     */
    private String strategyDesc;

    /**
     * 规则模型
     */
    private String ruleModels;

    public String[] getRuleModels(){
        if(ruleModels == null) return null;
        return ruleModels.split(",");
    }

    public String getRuleWeight(){
        String[] ruleModels = getRuleModels();
        for (String ruleModel : ruleModels) {
            if(ruleModel.equals("rule_weight")) return ruleModel;
        }
        return null;
    }

}
