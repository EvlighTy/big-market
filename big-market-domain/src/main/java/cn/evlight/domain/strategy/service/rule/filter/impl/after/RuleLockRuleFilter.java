package cn.evlight.domain.strategy.service.rule.filter.impl.after;

import cn.evlight.domain.strategy.model.entity.RuleFilterParamEntity;
import cn.evlight.domain.strategy.model.valobj.RuleFilterStateVO;
import cn.evlight.domain.strategy.repository.IStrategyRepository;
import cn.evlight.domain.strategy.service.rule.filter.AbstractAfterRuleFilter;
import cn.evlight.domain.strategy.service.rule.filter.factory.after.DefaultRuleFilterTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description: 达到阈值解锁奖品过滤器
 * @Author: evlight
 * @Date: 2024/5/26
 */

@Slf4j
@Component("rule_lock")
public class RuleLockRuleFilter extends AbstractAfterRuleFilter {

    @Autowired
    private IStrategyRepository strategyRepository;

    @Override
    public DefaultRuleFilterTreeFactory.Result doFilter(RuleFilterParamEntity ruleFilterParamEntity) {
        log.info("奖品解锁门槛检查...");
        //查询用户抽奖次数
        int raffleCount = 6;
        //查询奖项解锁阈值
        String strategyRuleValue = strategyRepository.getStrategyRuleValue(ruleFilterParamEntity.getStrategyId(), ruleFilterParamEntity.getAwardId(), ruleModel());
        if(raffleCount >= Integer.parseInt(strategyRuleValue)){
            //大于阈值放行
            return DefaultRuleFilterTreeFactory.Result.builder()
                    .state(RuleFilterStateVO.ALLOW)
                    .data(DefaultRuleFilterTreeFactory.StrategyAwardData.builder()
                            .awardId(ruleFilterParamEntity.getAwardId())
                            .build())
                    .build();
        }
        log.info("用户:{}抽奖次数不足", ruleFilterParamEntity.getUserId());
        return DefaultRuleFilterTreeFactory.Result.builder()
                .state(RuleFilterStateVO.TAKE_OVER)
                .data(DefaultRuleFilterTreeFactory.StrategyAwardData.builder()
                        .build())
                .build();
    }

    @Override
    protected String ruleModel() {
        return "rule_lock";
    }

}
