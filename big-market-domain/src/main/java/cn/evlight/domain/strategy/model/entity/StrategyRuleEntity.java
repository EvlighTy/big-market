package cn.evlight.domain.strategy.model.entity;

import cn.evlight.domain.strategy.service.ruleFilter.factory.before.DefaultRuleFilterChainFactory;
import cn.evlight.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 策略规则实体
 * @Author: evlight
 * @Date: 2024/5/25
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyRuleEntity {

    /**
     * 抽奖策略ID
     */
    private Long strategyId;

    /**
     * 抽奖奖品ID【规则类型为策略，则不需要奖品ID】
     */
    private Integer awardId;

    /**
     * 抽象规则类型；1-策略规则、2-奖品规则
     */
    private Boolean ruleType;

    /**
     * 抽奖规则类型【rule_random - 随机值计算、rule_lock - 抽奖几次后解锁、rule_luck_award - 幸运奖(兜底奖品)】
     */
    private String ruleModel;

    /**
     * 抽奖规则比值
     */
    private String ruleValue;

    /**
     * 抽奖规则描述
     */
    private String ruleDesc;

    /**
    * @Description: 获取策略权重值
    * @Param: []
    * @return:
    * @Date: 2024/5/25
    */
    public Map<Integer, List<Integer>> getRuleWeightValues(){
        if (!ruleModel.equals(DefaultRuleFilterChainFactory.RuleModel.RULE_WEIGHT.getCode()) || ruleValue == null || ruleValue.isEmpty()) return null;
        HashMap<Integer, List<Integer>> ruleValues = new HashMap<>();
        String[] thresholds = ruleValue.split(Constants.Split.SPACE);
        for (String threshold : thresholds) {
            String[] split = threshold.split(Constants.Split.COLON);
            if (split.length != 2) {
                throw new RuntimeException("invalid formatter");
            }
            List<Integer> values = Arrays.stream(split[1].split(Constants.Split.COMMA))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            ruleValues.put(Integer.parseInt(split[0]), values);
        }
        return ruleValues;
    }

    /**
     * @Description: 获取黑名单用户ID集合
     * @Param: []
     * @return:
     * @Date: 2024/5/26
     */
    public HashMap<Integer, List<String>> getRuleBlacklistValues(){
        if (!ruleModel.equals(DefaultRuleFilterChainFactory.RuleModel.RULE_BLACKLIST.getCode()) || ruleValue == null || ruleValue.isEmpty()) return null;
        HashMap<Integer, List<String>> ruleValues = new HashMap<>();
        String[] thresholds = ruleValue.split(Constants.Split.SPACE);
        for (String threshold : thresholds) {
            String[] split = threshold.split(Constants.Split.COLON);
            if (split.length != 2) {
                throw new RuntimeException("invalid formatter");
            }
            List<String> values = Arrays.stream(split[1].split(Constants.Split.COMMA))
                    .collect(Collectors.toList());
            ruleValues.put(Integer.parseInt(split[0]), values);
        }
        return ruleValues;
    }

}
