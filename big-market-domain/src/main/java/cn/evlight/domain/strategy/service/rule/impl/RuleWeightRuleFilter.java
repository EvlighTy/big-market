package cn.evlight.domain.strategy.service.rule.impl;

import cn.evlight.domain.strategy.model.entity.RuleFilterParamEntity;
import cn.evlight.domain.strategy.model.entity.RuleFilterResultEntity;
import cn.evlight.domain.strategy.model.entity.StrategyRuleEntity;
import cn.evlight.domain.strategy.model.valobj.RuleFilterStateVO;
import cn.evlight.domain.strategy.repository.IStrategyRepository;
import cn.evlight.domain.strategy.service.annotation.RaffleRuleModel;
import cn.evlight.domain.strategy.service.rule.IRuleFilter;
import cn.evlight.domain.strategy.service.rule.factory.DefaultRuleFilterFactory;
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
@Component
@RaffleRuleModel(rule_model = DefaultRuleFilterFactory.RuleModel.RULE_WIGHT)
public class RuleWeightRuleFilter implements IRuleFilter<RuleFilterResultEntity.BeforeRaffleRuleResult> {

    @Autowired
    private IStrategyRepository strategyRepository;

    @Override
    public RuleFilterResultEntity<RuleFilterResultEntity.BeforeRaffleRuleResult> doFilter(RuleFilterParamEntity ruleFilterParamEntity) {
        log.info("规则权重过滤...");
        //根据用户id查询积分值 todo
        ruleFilterParamEntity.setUserIntegral(6100L);

        StrategyRuleEntity strategyRuleEntity = strategyRepository.getStrategyRuleValue(ruleFilterParamEntity.getStrategyId(), ruleFilterParamEntity.getRuleModel());
        if(strategyRuleEntity == null){
            return RuleFilterResultEntity.<RuleFilterResultEntity.BeforeRaffleRuleResult>builder()
                    .stateCode(RuleFilterStateVO.ALLOW.getCode())
                    .stateInfo(RuleFilterStateVO.ALLOW.getInfo())
                    .build();
        }
        Map<String, Set<String>> ruleWeightValues = strategyRuleEntity.getRuleWeightValues();
        List<Integer> thresholdList = ruleWeightValues.keySet().stream()
                .map(Integer::parseInt)
                .sorted()
                .collect(Collectors.toList());
        Collections.reverse(thresholdList);
        Long userIntegral = ruleFilterParamEntity.getUserIntegral();
        Integer reachedThreshold = thresholdList.stream()
                .filter(threshold -> userIntegral >= threshold)
                .findFirst()
                .orElse(null);
        if (reachedThreshold != null){
            //达到某个门槛
            log.info("达到门槛值:{}", reachedThreshold);
            return RuleFilterResultEntity.<RuleFilterResultEntity.BeforeRaffleRuleResult>builder()
                    .stateCode(RuleFilterStateVO.TAKE_OVER.getCode())
                    .stateInfo(RuleFilterStateVO.TAKE_OVER.getInfo())
                    .ruleModel(DefaultRuleFilterFactory.RuleModel.RULE_WIGHT.getCode())
                    .result(RuleFilterResultEntity.BeforeRaffleRuleResult.builder()
                            .strategyId(ruleFilterParamEntity.getStrategyId())
                            .ruleWeightValueKey(reachedThreshold.toString()) //门槛值
                            .build())
                    .build();
        }
        return RuleFilterResultEntity.<RuleFilterResultEntity.BeforeRaffleRuleResult>builder()
                .stateCode(RuleFilterStateVO.ALLOW.getCode())
                .stateInfo(RuleFilterStateVO.ALLOW.getInfo())
                .build();
    }

}
