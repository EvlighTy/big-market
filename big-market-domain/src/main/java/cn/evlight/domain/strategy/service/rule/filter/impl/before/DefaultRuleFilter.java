package cn.evlight.domain.strategy.service.rule.filter.impl.before;

import cn.evlight.domain.strategy.model.entity.RuleFilterParamEntity;
import cn.evlight.domain.strategy.service.armory.IUserStrategyArmory;
import cn.evlight.domain.strategy.service.rule.filter.AbstractBeforeRuleFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description: 默认抽奖规则过滤器
 * @Author: evlight
 * @Date: 2024/5/27
 */

@Slf4j
@Component("default")
public class DefaultRuleFilter extends AbstractBeforeRuleFilter {

    @Autowired
    private IUserStrategyArmory userStrategyArmory;

    @Override
    public Integer doFilter(RuleFilterParamEntity ruleFilterParamEntity) {
        log.info("执行默认抽奖规则...");
        return userStrategyArmory.getRandomAwardId(ruleFilterParamEntity.getStrategyId());
    }

    @Override
    protected String ruleModel() {
        return "default";
    }

}
