package cn.evlight.domain.strategy.service.raffle;

import cn.evlight.domain.strategy.model.entity.*;
import cn.evlight.domain.strategy.model.valobj.AwardRuleModelVO;
import cn.evlight.domain.strategy.model.valobj.RuleFilterStateVO;
import cn.evlight.domain.strategy.repository.IStrategyRepository;
import cn.evlight.domain.strategy.service.IRaffleStrategy;
import cn.evlight.domain.strategy.service.armory.IUserStrategyArmory;
import cn.evlight.domain.strategy.service.rule.factory.DefaultRuleFilterFactory;
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
    protected IStrategyRepository strategyRepository;

    @Autowired
    protected IUserStrategyArmory userStrategyArmory;

    @Override
    public RaffleResultEntity doRaffle(RaffleParamEntity raffleParamEntity) {
        //参数校验
        Long strategyId = raffleParamEntity.getStrategyId();
        Long userId = raffleParamEntity.getUserId();
        if(strategyId == null || userId == null) throw new AppException("invalid params");

        /*抽奖前逻辑*/
        //查询策略规则模型
        StrategyEntity strategyEntity = strategyRepository.getStrategyEntity(strategyId);
        //抽奖前置过滤器过滤
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
        raffleParamEntity.setAwardId(awardId);
        /*抽奖中逻辑*/
        //查询奖项规则模型
        AwardRuleModelVO awardRuleModels = strategyRepository.getAwardRuleModels(strategyId, awardId);
        //中置过滤器过滤
        resultEntity = duringRaffle(raffleParamEntity, awardRuleModels.getDuringRuleModels());
        if (resultEntity.getStateCode().equals(RuleFilterStateVO.TAKE_OVER.getCode())){
            //兜底逻辑 todo
            log.info("未达到奖项解锁阈值，触发兜底逻辑");
        }

        return RaffleResultEntity.builder()
                .awardId(awardId)
                .build();
    }

    public abstract RuleFilterResultEntity<RuleFilterResultEntity.BeforeRaffleRuleResult> beforeRaffle(RaffleParamEntity raffleParamEntity, String... ruleModels);
    public abstract RuleFilterResultEntity<RuleFilterResultEntity.BeforeRaffleRuleResult> duringRaffle(RaffleParamEntity raffleParamEntity, String... ruleModels);

}
