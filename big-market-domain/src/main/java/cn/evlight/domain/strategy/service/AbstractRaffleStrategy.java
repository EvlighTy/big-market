package cn.evlight.domain.strategy.service;

import cn.evlight.domain.strategy.model.entity.RaffleParamEntity;
import cn.evlight.domain.strategy.model.entity.RaffleResultEntity;
import cn.evlight.domain.strategy.model.entity.RuleFilterParamEntity;
import cn.evlight.domain.strategy.model.entity.RuleFilterResultEntity;
import cn.evlight.domain.strategy.model.valobj.AwardRuleModelVO;
import cn.evlight.domain.strategy.repository.IStrategyRepository;
import cn.evlight.domain.strategy.service.armory.IUserStrategyArmory;
import cn.evlight.domain.strategy.service.rule.filter.AbstractBeforeRuleFilter;
import cn.evlight.domain.strategy.service.rule.filter.factory.before.DefaultRuleFilterChainFactory;
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

    @Autowired
    private DefaultRuleFilterChainFactory ruleFilterChainFactory;

    @Override
    public RaffleResultEntity doRaffle(RaffleParamEntity raffleParamEntity) {
        //参数校验
        Long strategyId = raffleParamEntity.getStrategyId();
        Long userId = raffleParamEntity.getUserId();
        if(strategyId == null || userId == null) throw new AppException("invalid params");

        /*抽奖前逻辑*/
        RuleFilterParamEntity ruleFilterParamEntity = RuleFilterParamEntity.builder()
                .strategyId(strategyId)
                .userId(userId)
                .build();
        AbstractBeforeRuleFilter ruleFilter = ruleFilterChainFactory.openRuleFilterChain(raffleParamEntity.getStrategyId());
        Integer awardId = ruleFilter.doFilter(ruleFilterParamEntity);
        ruleFilterParamEntity.setAwardId(awardId);
        /*抽奖中逻辑*/
        //查询奖项规则模型
        AwardRuleModelVO awardRuleModels = strategyRepository.getAwardRuleModels(strategyId, awardId);
        //中置过滤器过滤
/*        RuleFilterResultEntity<RuleFilterResultEntity.BeforeRaffleRuleResult> resultEntity = duringRaffle(raffleParamEntity, awardRuleModels.getDuringRuleModels());
        if (resultEntity.getStateCode().equals(RuleFilterStateVO.TAKE_OVER.getCode())){
            //兜底逻辑 todo
            log.info("未达到奖项解锁阈值，触发兜底逻辑");
        }*/
        return RaffleResultEntity.builder()
                .awardId(awardId)
                .build();
    }

    public abstract RuleFilterResultEntity<RuleFilterResultEntity.BeforeRaffleRuleResult> duringRaffle(RaffleParamEntity raffleParamEntity, String... ruleModels);

}
