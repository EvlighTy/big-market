package cn.evlight.domain.strategy.service.rule.impl;

import cn.evlight.domain.strategy.model.entity.RuleFilterParamEntity;
import cn.evlight.domain.strategy.model.entity.RuleFilterResultEntity;
import cn.evlight.domain.strategy.model.valobj.RuleFilterStateVO;
import cn.evlight.domain.strategy.repository.IStrategyRepository;
import cn.evlight.domain.strategy.service.annotation.RaffleRuleModel;
import cn.evlight.domain.strategy.service.rule.IRuleFilter;
import cn.evlight.domain.strategy.service.rule.factory.DefaultRuleFilterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description: 达到阈值解锁奖品过滤器
 * @Author: evlight
 * @Date: 2024/5/26
 */

@Slf4j
@Component
@RaffleRuleModel(rule_model = DefaultRuleFilterFactory.RuleModel.RULE_LOCK)
public class RuleLockRuleFilter implements IRuleFilter<RuleFilterResultEntity.DuringRaffleRuleResult> {

    @Autowired
    private IStrategyRepository strategyRepository;

    @Override
    public RuleFilterResultEntity<RuleFilterResultEntity.DuringRaffleRuleResult> doFilter(RuleFilterParamEntity ruleFilterParamEntity) {
        log.info("阈值解锁过滤中...");
        //查询用户抽奖次数
        int raffleCount = 5;
        //查询奖项解锁阈值
        String strategyRuleValue = strategyRepository.getStrategyRuleValue(ruleFilterParamEntity.getStrategyId(), ruleFilterParamEntity.getAwardId(), ruleFilterParamEntity.getRuleModel());
        if(raffleCount >= Integer.parseInt(strategyRuleValue)){
            //大于阈值放行
            return RuleFilterResultEntity.<RuleFilterResultEntity.DuringRaffleRuleResult>builder()
                    .stateCode(RuleFilterStateVO.ALLOW.getCode())
                    .stateInfo(RuleFilterStateVO.ALLOW.getInfo())
                    .build();
        }
        return RuleFilterResultEntity.<RuleFilterResultEntity.DuringRaffleRuleResult>builder()
                .stateCode(RuleFilterStateVO.TAKE_OVER.getCode())
                .stateInfo(RuleFilterStateVO.TAKE_OVER.getInfo())
                .build();
    }

}
