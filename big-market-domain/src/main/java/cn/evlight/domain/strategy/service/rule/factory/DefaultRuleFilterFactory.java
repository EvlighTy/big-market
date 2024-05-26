package cn.evlight.domain.strategy.service.rule.factory;

import cn.evlight.domain.strategy.model.entity.RuleFilterResultEntity;
import cn.evlight.domain.strategy.service.annotation.RaffleRuleModel;
import cn.evlight.domain.strategy.service.rule.IRuleFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 规则工厂
 * @create 2023-12-31 11:23
 */
@Service
public class DefaultRuleFilterFactory {

    public Map<String, IRuleFilter<?>> ruleFilterMap = new ConcurrentHashMap<>();

    public DefaultRuleFilterFactory(List<IRuleFilter<?>> logicFilters) {
        logicFilters.forEach(filter -> {
            RaffleRuleModel raffleRuleModel = AnnotationUtils.findAnnotation(filter.getClass(), RaffleRuleModel.class);
            if (raffleRuleModel != null) {
                ruleFilterMap.put(raffleRuleModel.rule_model().getCode(), filter);
            }
        });
    }

    public <T extends RuleFilterResultEntity.RuleFilterResult> Map<String, IRuleFilter<T>> getRuleFilterMap() {
        return (Map<String, IRuleFilter<T>>) (Map<?, ?>) ruleFilterMap;
    }

    @Getter
    @AllArgsConstructor
    public enum RuleModel {

        RULE_WIGHT("rule_weight", "权重抽奖", "before"),
        RULE_BLACKLIST("rule_blacklist", "黑名单抽奖", "before"),
        RULE_LOCK("rule_lock", "解锁抽奖", "during"),
        RULE_LUCKY("rule_lucky", "兜底抽奖", "after"),
        ;

        private final String code;
        private final String info;
        private final String type;

        public static boolean isDuringRuleModel(String ruleModel){
            try {
                return RuleModel.valueOf(ruleModel.toUpperCase()).type.equals("during");
            }catch (Exception e){
                return false;
            }
        }

        public static boolean isAfterRuleModel(String ruleModel){
            try {
                return RuleModel.valueOf(ruleModel.toUpperCase()).type.equals("after");
            }catch (Exception e){
                return false;
            }
        }

    }

}
