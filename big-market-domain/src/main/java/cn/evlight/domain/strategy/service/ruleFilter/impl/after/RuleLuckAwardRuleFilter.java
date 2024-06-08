package cn.evlight.domain.strategy.service.ruleFilter.impl.after;

import cn.evlight.domain.strategy.model.entity.RuleFilterParamEntity;
import cn.evlight.domain.strategy.model.valobj.RuleFilterStateVO;
import cn.evlight.domain.strategy.service.ruleFilter.AbstractAfterRuleFilter;
import cn.evlight.domain.strategy.service.ruleFilter.factory.after.DefaultRuleFilterTreeFactory;
import cn.evlight.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Description: 兜底奖品过滤器
 * @Author: evlight
 * @Date: 2024/5/26
 */

@Slf4j
@Component("rule_luck_award")
public class RuleLuckAwardRuleFilter extends AbstractAfterRuleFilter {

    @Override
    public DefaultRuleFilterTreeFactory.Result doFilter(RuleFilterParamEntity ruleFilterParamEntity) {
        log.info("策略奖品兜底...");
        String[] split = ruleFilterParamEntity.getRuleValue().split(Constants.Split.COLON);
        if (split.length == 0) {
            log.error("规则过滤-兜底奖品，兜底奖品未配置告警");
            throw new RuntimeException("兜底奖品未配置");
        }
        //兜底奖励配置
        Integer awardId = Integer.valueOf(split[0]);
        String awardRuleValue = split.length > 1 ? split[1] : "";
        //返回兜底奖品
        log.info("规则过滤-兜底奖品");
        return DefaultRuleFilterTreeFactory.Result.builder()
                .state(RuleFilterStateVO.TAKE_OVER)
                .data(DefaultRuleFilterTreeFactory.ResultData.builder()
                        .awardId(awardId)
                        .awardRuleValue(awardRuleValue)
                        .build())
                .build();
    }

    @Override
    protected String ruleModel() {
        return "rule_luck_award";
    }

}
