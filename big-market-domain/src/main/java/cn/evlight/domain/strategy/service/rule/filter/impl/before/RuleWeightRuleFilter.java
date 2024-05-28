package cn.evlight.domain.strategy.service.rule.filter.impl.before;

import cn.evlight.domain.strategy.model.entity.RuleFilterParamEntity;
import cn.evlight.domain.strategy.model.entity.StrategyRuleEntity;
import cn.evlight.domain.strategy.repository.IStrategyRepository;
import cn.evlight.domain.strategy.service.armory.IUserStrategyArmory;
import cn.evlight.domain.strategy.service.rule.filter.AbstractBeforeRuleFilter;
import cn.evlight.domain.strategy.service.rule.filter.factory.before.DefaultRuleFilterChainFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description: 权重抽奖过滤器
 * @Author: evlight
 * @Date: 2024/5/26
 */

@Slf4j
@Component("rule_weight")
public class RuleWeightRuleFilter extends AbstractBeforeRuleFilter {

    @Autowired
    private IStrategyRepository strategyRepository;

    @Autowired
    private IUserStrategyArmory userStrategyArmory;

    @Override
    public DefaultRuleFilterChainFactory.ResultData doFilter(RuleFilterParamEntity ruleFilterParamEntity) {
        log.info("规则权重过滤...");
        //查询用户积分值 todo
        long userScore = 2000L;

        //查询策略权重规则
        StrategyRuleEntity strategyRuleEntity = strategyRepository.getStrategyRuleValue(ruleFilterParamEntity.getStrategyId(), ruleModel());
        if(strategyRuleEntity == null){
            //没有配置权重规则
            return next().doFilter(ruleFilterParamEntity);
        }
        Map<String, Set<String>> ruleWeightValues = strategyRuleEntity.getRuleWeightValues();
        List<Integer> thresholdList = ruleWeightValues.keySet().stream()
                .map(Integer::parseInt)
                .sorted()
                .collect(Collectors.toList());
        Collections.reverse(thresholdList);
        Integer reachedThreshold = thresholdList.stream()
                .filter(threshold -> userScore >= threshold)
                .findFirst()
                .orElse(null);
        if (reachedThreshold != null){
            //达到某个门槛
            log.info("达到门槛值:{}", reachedThreshold);
            //权重抽奖
            String key = ruleFilterParamEntity.getStrategyId() + ":" + reachedThreshold;
            Integer awardId = userStrategyArmory.getRandomAwardId(key);
            return DefaultRuleFilterChainFactory.ResultData.builder()
                    .awardId(awardId)
                    .ruleModel(ruleModel())
                    .build();
        }
        //放行
        return next().doFilter(ruleFilterParamEntity);
    }

    @Override
    protected String ruleModel() {
        return DefaultRuleFilterChainFactory.RuleModel.RULE_WEIGHT.getCode();
    }

}
