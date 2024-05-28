package cn.evlight.domain.strategy.service.rule.filter.factory.before;

import cn.evlight.domain.strategy.model.entity.StrategyEntity;
import cn.evlight.domain.strategy.repository.IStrategyRepository;
import cn.evlight.domain.strategy.service.rule.filter.AbstractBeforeRuleFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
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


    @Getter
    @AllArgsConstructor
    public enum RuleModel {

        RULE_LOCK("rule_lock", "【抽奖中规则】抽奖n次后，对应奖品可解锁抽奖", "during"),
        RULE_LUCK_AWARD("rule_luck_award", "【抽奖后规则】抽奖n次后，对应奖品可解锁抽奖", "after"),
        ;

        private final String code;
        private final String info;
        private final String type;

        public static boolean isDuringRuleModel(String code){
            return "during".equals(RuleModel.valueOf(code.toUpperCase()).type);
        }

        public static boolean isAfterRuleModel(String code){
            return "after".equals(RuleModel.valueOf(code.toUpperCase()).type);
        }

    }
}
