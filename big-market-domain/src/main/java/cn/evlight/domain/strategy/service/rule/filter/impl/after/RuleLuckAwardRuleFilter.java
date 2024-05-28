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
 * @Description: 兜底奖品过滤器
 * @Author: evlight
 * @Date: 2024/5/26
 */

@Slf4j
@Component("rule_luck_award")
public class RuleLuckAwardRuleFilter extends AbstractAfterRuleFilter {

    @Autowired
    private IStrategyRepository strategyRepository;

    @Override
    public DefaultRuleFilterTreeFactory.Result doFilter(RuleFilterParamEntity ruleFilterParamEntity) {
        log.info("奖项兜底...");
        return DefaultRuleFilterTreeFactory.Result.builder()
                .state(RuleFilterStateVO.TAKE_OVER)
                .data(DefaultRuleFilterTreeFactory.ResultData.builder()
                        .awardRuleValue("1,100")
                        .build())
                .build();
    }

    @Override
    protected String ruleModel() {
        return "rule_luck_award";
    }

}
