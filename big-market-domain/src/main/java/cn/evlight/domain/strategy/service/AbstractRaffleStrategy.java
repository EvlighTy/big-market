package cn.evlight.domain.strategy.service;

import cn.evlight.domain.strategy.model.entity.RaffleParamEntity;
import cn.evlight.domain.strategy.model.entity.RaffleResultEntity;
import cn.evlight.domain.strategy.model.entity.RuleFilterParamEntity;
import cn.evlight.domain.strategy.model.entity.StrategyAwardEntity;
import cn.evlight.domain.strategy.repository.IStrategyRepository;
import cn.evlight.domain.strategy.service.ruleFilter.factory.after.DefaultRuleFilterTreeFactory;
import cn.evlight.domain.strategy.service.ruleFilter.factory.before.DefaultRuleFilterChainFactory;
import cn.evlight.types.common.Constants;
import cn.evlight.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 抽奖策略抽象类
 * @Author: evlight
 * @Date: 2024/5/25
 */

@Slf4j
public abstract class AbstractRaffleStrategy implements IRaffleStrategy {

    @Autowired
    private IStrategyRepository strategyRepository;

    @Override
    public RaffleResultEntity doRaffle(RaffleParamEntity raffleParamEntity) {
        //参数校验
        Long strategyId = raffleParamEntity.getStrategyId();
        String userId = raffleParamEntity.getUserId();
        if(strategyId == null || userId == null){
            throw new AppException(Constants.ExceptionInfo.INVALID_PARAMS);
        }
        RuleFilterParamEntity ruleFilterParamEntity = RuleFilterParamEntity.builder()
                .strategyId(strategyId)
                .userId(userId)
                .build();
        //前置过滤器
        DefaultRuleFilterChainFactory.ResultData beforeRaffleResult = doBeforeRaffle(ruleFilterParamEntity);
        Integer awardId = beforeRaffleResult.getAwardId();
        if(beforeRaffleResult.getRuleModel().equals(DefaultRuleFilterChainFactory.RuleModel.RULE_BLACKLIST.getCode())){
            //黑名单用户
            return buildResult(strategyId, awardId, beforeRaffleResult.getAwardConfig());
        }
        ruleFilterParamEntity.setAwardId(awardId);
        //后置过滤器
        DefaultRuleFilterTreeFactory.ResultData afterRaffleResult = doAfterRaffle(ruleFilterParamEntity);
        return buildResult(strategyId, afterRaffleResult.getAwardId(), afterRaffleResult.getAwardRuleValue());
    }

    /**
    * @Description: 构造返回结果
    * @Param: [strategyId, awardId, awardRuleValue]
    * @return:
    * @Date: 2024/5/30
    */
    private RaffleResultEntity buildResult(Long strategyId, Integer awardId, String awardConfig) {
        //查询策略奖品实体
        StrategyAwardEntity strategyAwardEntity = strategyRepository.getStrategyAwardEntity(strategyId, awardId);
        return RaffleResultEntity.builder()
                .awardId(awardId)
                .sort(strategyAwardEntity.getSort())
                .awardConfig(awardConfig)
                .awardTitle(strategyAwardEntity.getAwardTitle())
                .build();
    }

    public abstract DefaultRuleFilterChainFactory.ResultData doBeforeRaffle(RuleFilterParamEntity ruleFilterParamEntity);

    public abstract DefaultRuleFilterTreeFactory.ResultData doAfterRaffle(RuleFilterParamEntity ruleFilterParamEntity);

}
