package cn.evlight.domain.strategy.service.raffle;

import cn.evlight.domain.strategy.model.entity.RaffleParamEntity;
import cn.evlight.domain.strategy.model.entity.RuleFilterParamEntity;
import cn.evlight.domain.strategy.model.entity.RuleFilterResultEntity;
import cn.evlight.domain.strategy.model.valobj.RuleFilterStateVO;
import cn.evlight.domain.strategy.service.rule.IRuleFilter;
import cn.evlight.domain.strategy.service.rule.factory.DefaultRuleFilterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 默认抽奖策略实现
 * @Author: evlight
 * @Date: 2024/5/26
 */

@Service
public class DefaultRaffleStrategy extends AbstractRaffleStrategy{

    @Autowired
    private DefaultRuleFilterFactory ruleFilterFactory;

    @Override
    public RuleFilterResultEntity<RuleFilterResultEntity.BeforeRaffleRuleResult> beforeRaffle(RaffleParamEntity raffleParamEntity, String... ruleModels) {
        //如果规则模型为空则直接放行
        RuleFilterResultEntity<RuleFilterResultEntity.BeforeRaffleRuleResult> resultEntity = RuleFilterResultEntity.<RuleFilterResultEntity.BeforeRaffleRuleResult>builder()
                .stateCode(RuleFilterStateVO.ALLOW.getCode())
                .stateInfo(RuleFilterStateVO.ALLOW.getInfo())
                .build();
        if(ruleModels == null || ruleModels.length == 0){
            return resultEntity;
        }
        /*执行过滤器*/
        Map<String, IRuleFilter<RuleFilterResultEntity.BeforeRaffleRuleResult>> ruleFilterMap = ruleFilterFactory.getRuleFilterMap();
        //执行黑名单过滤器
        String isBlacklist = Arrays.stream(ruleModels)
                .filter(ruleModel -> ruleModel.equals(DefaultRuleFilterFactory.RuleModel.RULE_BLACKLIST.getCode()))
                .findFirst()
                .orElse(null);
        if(isBlacklist != null){
            IRuleFilter<RuleFilterResultEntity.BeforeRaffleRuleResult> ruleFilter = ruleFilterMap.get(DefaultRuleFilterFactory.RuleModel.RULE_BLACKLIST.getCode());
            RuleFilterParamEntity ruleFilterParamEntity = RuleFilterParamEntity.builder()
                    .strategyId(raffleParamEntity.getStrategyId())
                    .userId(raffleParamEntity.getUserId())
                    .ruleModel(DefaultRuleFilterFactory.RuleModel.RULE_BLACKLIST.getCode())
                    .build();

            resultEntity = ruleFilter.doFilter(ruleFilterParamEntity);
            if(resultEntity.getStateCode().equals(RuleFilterStateVO.TAKE_OVER.getCode())){
                return resultEntity;
            }
        }
        //执行其他过滤器
        List<String> ruleList = Arrays.stream(ruleModels)
                .filter(ruleModel -> !ruleModel.equals(DefaultRuleFilterFactory.RuleModel.RULE_BLACKLIST.getCode()))
                .collect(Collectors.toList());
        if(ruleList.isEmpty()){
            return resultEntity;
        }
        for (String rule : ruleList) {
            IRuleFilter<RuleFilterResultEntity.BeforeRaffleRuleResult> ruleFilter = ruleFilterMap.get(rule);
            RuleFilterParamEntity ruleFilterParamEntity = RuleFilterParamEntity.builder()
                    .strategyId(raffleParamEntity.getStrategyId())
                    .userId(raffleParamEntity.getUserId())
                    .ruleModel(rule)
                    .build();
            resultEntity = ruleFilter.doFilter(ruleFilterParamEntity);
            if(resultEntity.getStateCode().equals(RuleFilterStateVO.TAKE_OVER.getCode())){
                return resultEntity;
            }
        }
        return resultEntity;
    }

    @Override
    public RuleFilterResultEntity<RuleFilterResultEntity.BeforeRaffleRuleResult> duringRaffle(RaffleParamEntity raffleParamEntity, String... ruleModels) {
        //如果规则模型为空则直接放行
        RuleFilterResultEntity<RuleFilterResultEntity.BeforeRaffleRuleResult> resultEntity = RuleFilterResultEntity.<RuleFilterResultEntity.BeforeRaffleRuleResult>builder()
                .stateCode(RuleFilterStateVO.ALLOW.getCode())
                .stateInfo(RuleFilterStateVO.ALLOW.getInfo())
                .build();
        if(ruleModels == null || ruleModels.length == 0){
            return resultEntity;
        }
        /*执行过滤器*/
        Map<String, IRuleFilter<RuleFilterResultEntity.BeforeRaffleRuleResult>> ruleFilterMap = ruleFilterFactory.getRuleFilterMap();
        for (String rule : ruleModels) {
            IRuleFilter<RuleFilterResultEntity.BeforeRaffleRuleResult> ruleFilter = ruleFilterMap.get(rule);
            RuleFilterParamEntity ruleFilterParamEntity = RuleFilterParamEntity.builder()
                    .strategyId(raffleParamEntity.getStrategyId())
                    .userId(raffleParamEntity.getUserId())
                    .awardId(raffleParamEntity.getAwardId())
                    .ruleModel(rule)
                    .build();
            resultEntity = ruleFilter.doFilter(ruleFilterParamEntity);
            if(resultEntity.getStateCode().equals(RuleFilterStateVO.TAKE_OVER.getCode())){
                return resultEntity;
            }
        }
        return resultEntity;
    }
}
