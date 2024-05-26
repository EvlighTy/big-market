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

import java.util.HashMap;
import java.util.Set;

/**
 * @Description: 黑名单过滤器
 * @Author: evlight
 * @Date: 2024/5/26
 */

@Slf4j
@Component
@RaffleRuleModel(rule_model = DefaultRuleFilterFactory.RuleModel.RULE_BLACKLIST)
public class RuleBlackListRuleFilter implements IRuleFilter<RuleFilterResultEntity.BeforeRaffleRuleResult> {

    @Autowired
    private IStrategyRepository strategyRepository;

    @Override
    public RuleFilterResultEntity<RuleFilterResultEntity.BeforeRaffleRuleResult> doFilter(RuleFilterParamEntity ruleFilterParamEntity) {
        log.info("黑名单过滤...");
        StrategyRuleEntity strategyRuleEntity = strategyRepository.getStrategyRuleValue(ruleFilterParamEntity.getStrategyId(), ruleFilterParamEntity.getRuleModel());
        HashMap<String, Set<String>> ruleBlacklistValues = strategyRuleEntity.getRuleBlacklistValues();
        //过滤黑名单用户
        for (String key : ruleBlacklistValues.keySet()) {
            Set<String> blacklist = ruleBlacklistValues.get(key);
            if (blacklist.contains(ruleFilterParamEntity.getUserId().toString())){
                //是黑名单用户
                log.info("黑名单用户被拦截:{}", ruleFilterParamEntity.getUserId());
                return RuleFilterResultEntity.<RuleFilterResultEntity.BeforeRaffleRuleResult>builder()
                        .stateCode(RuleFilterStateVO.TAKE_OVER.getCode())
                        .stateInfo(RuleFilterStateVO.TAKE_OVER.getInfo())
                        .ruleModel(DefaultRuleFilterFactory.RuleModel.RULE_BLACKLIST.getCode())
                        .result(RuleFilterResultEntity.BeforeRaffleRuleResult.builder()
                                .strategyId(ruleFilterParamEntity.getStrategyId())
                                .awardId(Integer.parseInt(key)) //奖品ID
                                .build())
                        .build();
            }
        }
        //执行其他过滤器过滤逻辑
        return RuleFilterResultEntity.<RuleFilterResultEntity.BeforeRaffleRuleResult>builder()
                .stateCode(RuleFilterStateVO.ALLOW.getCode())
                .stateInfo(RuleFilterStateVO.ALLOW.getInfo())
                .build();
    }

}
