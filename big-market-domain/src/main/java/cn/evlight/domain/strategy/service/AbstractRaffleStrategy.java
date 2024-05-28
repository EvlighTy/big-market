package cn.evlight.domain.strategy.service;

import cn.evlight.domain.strategy.model.entity.RaffleParamEntity;
import cn.evlight.domain.strategy.model.entity.RaffleResultEntity;
import cn.evlight.domain.strategy.model.entity.RuleFilterParamEntity;
import cn.evlight.domain.strategy.service.rule.filter.factory.after.DefaultRuleFilterTreeFactory;
import cn.evlight.domain.strategy.service.rule.filter.factory.before.DefaultRuleFilterChainFactory;
import cn.evlight.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 抽奖策略抽象类
 * @Author: evlight
 * @Date: 2024/5/25
 */

@Slf4j
public abstract class AbstractRaffleStrategy implements IRaffleStrategy {

    @Override
    public RaffleResultEntity doRaffle(RaffleParamEntity raffleParamEntity) {
        //参数校验
        Long strategyId = raffleParamEntity.getStrategyId();
        Long userId = raffleParamEntity.getUserId();
        if(strategyId == null || userId == null) throw new AppException("invalid params");

        RuleFilterParamEntity ruleFilterParamEntity = RuleFilterParamEntity.builder()
                .strategyId(strategyId)
                .userId(userId)
                .build();
        //前置过滤器
        DefaultRuleFilterChainFactory.ResultData beforeRaffleResult = doBeforeRaffle(ruleFilterParamEntity);
        Integer awardId = beforeRaffleResult.getAwardId();
        if(beforeRaffleResult.getRuleModel().equals(DefaultRuleFilterChainFactory.RuleModel.RULE_BLACKLIST.getCode())){
            //黑名单用户
            return RaffleResultEntity.builder()
                    .awardId(awardId)
                    .build();
        }
        ruleFilterParamEntity.setAwardId(awardId);
        //后置过滤器
        DefaultRuleFilterTreeFactory.ResultData afterRaffleResult = doAfterRaffle(ruleFilterParamEntity);
        return RaffleResultEntity.builder()
                .awardId(awardId)
                .awardConfig(afterRaffleResult.getAwardRuleValue())
                .build();
    }

    public abstract DefaultRuleFilterChainFactory.ResultData doBeforeRaffle(RuleFilterParamEntity ruleFilterParamEntity);

    public abstract DefaultRuleFilterTreeFactory.ResultData doAfterRaffle(RuleFilterParamEntity ruleFilterParamEntity);

}
