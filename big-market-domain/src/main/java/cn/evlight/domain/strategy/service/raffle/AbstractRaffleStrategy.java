package cn.evlight.domain.strategy.service.raffle;

import cn.evlight.domain.strategy.model.entity.*;
import cn.evlight.domain.strategy.model.valobj.RuleFilterStateVO;
import cn.evlight.domain.strategy.repository.IStrategyRepository;
import cn.evlight.domain.strategy.service.IRaffleStrategy;
import cn.evlight.domain.strategy.service.armory.IUserStrategyArmory;
import cn.evlight.domain.strategy.service.rule.factory.DefaultRuleFilterFactory;
import cn.evlight.types.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 抽奖策略抽象类
 * @Author: evlight
 * @Date: 2024/5/25
 */
public abstract class AbstractRaffleStrategy implements IRaffleStrategy {

    @Autowired
    protected IStrategyRepository strategyRepository;

    @Autowired
    protected IUserStrategyArmory userStrategyArmory;

    @Override
    public RaffleResultEntity doRaffle(RaffleParamEntity raffleParamEntity) {
        //参数校验
        Long strategyId = raffleParamEntity.getStrategyId();
        Long userId = raffleParamEntity.getUserId();
        if(strategyId == null || userId == null) throw new AppException("invalid params");

        StrategyEntity strategyEntity = strategyRepository.getStrategyEntity(strategyId);
        RuleFilterResultEntity<RuleFilterResultEntity.BeforeRaffleRuleResult> resultEntity = beforeRaffle(raffleParamEntity, strategyEntity.getRuleModels());
        if(resultEntity.getStateCode().equals(RuleFilterStateVO.TAKE_OVER.getCode())){
            //被过滤器接管
            if(resultEntity.getRuleModel().equals(DefaultRuleFilterFactory.RuleModel.RULE_BLACKLIST.getCode())){
                //黑名单
                return RaffleResultEntity.builder()
                        .awardId(resultEntity.getResult().getAwardId())
                        .build();

            }else if (resultEntity.getRuleModel().equals(DefaultRuleFilterFactory.RuleModel.RULE_WIGHT.getCode())){
                //权重抽奖
                String ruleWeightValueKey = resultEntity.getResult().getRuleWeightValueKey();
                String key = strategyId + ":" +ruleWeightValueKey;
                Integer awardId = userStrategyArmory.getRandomAwardId(key);
                return RaffleResultEntity.builder()
                        .awardId(awardId)
                        .build();
            }
        }
        //默认抽奖流程
        Integer awardId = userStrategyArmory.getRandomAwardId(strategyId);
        return RaffleResultEntity.builder()
                .awardId(awardId)
                .build();
    }

    public abstract RuleFilterResultEntity<RuleFilterResultEntity.BeforeRaffleRuleResult> beforeRaffle(RaffleParamEntity raffleParamEntity, String... ruleModels);

}
