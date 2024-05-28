package cn.evlight.domain.strategy.service.raffle;

import cn.evlight.domain.strategy.model.entity.RaffleParamEntity;
import cn.evlight.domain.strategy.model.entity.RuleFilterResultEntity;
import cn.evlight.domain.strategy.service.AbstractRaffleStrategy;
import org.springframework.stereotype.Service;

/**
 * @Description: 默认抽奖实现类
 * @Author: evlight
 * @Date: 2024/5/27
 */

@Service
public class DefaultRaffleStrategy extends AbstractRaffleStrategy {
    @Override
    public RuleFilterResultEntity<RuleFilterResultEntity.BeforeRaffleRuleResult> duringRaffle(RaffleParamEntity raffleParamEntity, String... ruleModels) {
        return null;
    }
}
