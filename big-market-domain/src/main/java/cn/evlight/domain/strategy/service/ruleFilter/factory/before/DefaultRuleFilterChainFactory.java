package cn.evlight.domain.strategy.service.ruleFilter.factory.before;

import cn.evlight.domain.strategy.model.entity.StrategyEntity;
import cn.evlight.domain.strategy.repository.IStrategyRepository;
import cn.evlight.domain.strategy.service.ruleFilter.AbstractBeforeRuleFilter;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description: 默认过滤器链工厂
 * @Author: evlight
 * @Date: 2024/5/27
 */

@Component
public class DefaultRuleFilterChainFactory {

    @Autowired
    private Map<String, AbstractBeforeRuleFilter> ruleFilterMap;

    @Autowired
    private IStrategyRepository strategyRepository;

    public AbstractBeforeRuleFilter openRuleFilterChain(Long strategyId){
        //查询策略对应的规则模型
        StrategyEntity strategyEntity = strategyRepository.getStrategyEntity(strategyId);
        String[] ruleModels = strategyEntity.getRuleModels();
        AbstractBeforeRuleFilter defaultRuleFilter = ruleFilterMap.get("default");
        if(ruleModels == null || ruleModels.length == 0){
            //没有配置策略规则
            return defaultRuleFilter;
        }
        AbstractBeforeRuleFilter head = ruleFilterMap.get(ruleModels[0]);
        AbstractBeforeRuleFilter current = head;
        for (int i = 1; i < ruleModels.length; i++) {
            AbstractBeforeRuleFilter ruleFilter = ruleFilterMap.get(ruleModels[i]);
            current = current.addNext(ruleFilter);
        }
        current.addNext(defaultRuleFilter);
        return head;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResultData {
        /** 抽奖奖品ID - 内部流转使用 */
        private Integer awardId;
        /**  */
        private String ruleModel;
    }

    @Getter
    @AllArgsConstructor
    public enum RuleModel {

        RULE_DEFAULT("rule_default", "默认抽奖"),
        RULE_BLACKLIST("rule_blacklist", "黑名单抽奖"),
        RULE_WEIGHT("rule_weight", "权重规则"),
        ;

        private final String code;
        private final String info;

    }

}
