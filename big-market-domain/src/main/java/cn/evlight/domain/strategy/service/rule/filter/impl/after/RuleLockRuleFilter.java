package cn.evlight.domain.strategy.service.rule.filter.impl.after;

import cn.evlight.domain.strategy.model.entity.RuleFilterParamEntity;
import cn.evlight.domain.strategy.model.valobj.RuleFilterStateVO;
import cn.evlight.domain.strategy.service.rule.filter.AbstractAfterRuleFilter;
import cn.evlight.domain.strategy.service.rule.filter.factory.after.DefaultRuleFilterTreeFactory;
import cn.evlight.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Description: 达到阈值解锁奖品过滤器
 * @Author: evlight
 * @Date: 2024/5/26
 */

@Slf4j
@Component("rule_lock")
public class RuleLockRuleFilter extends AbstractAfterRuleFilter {

    @Override
    public DefaultRuleFilterTreeFactory.Result doFilter(RuleFilterParamEntity ruleFilterParamEntity) {
        log.info("奖品解锁门槛检查...");
        //查询用户抽奖次数
        int raffleCount = 6;
        //查询奖项解锁阈值
        String ruleValue = ruleFilterParamEntity.getRuleValue();
        if(ruleValue == null){
            throw new AppException("奖品门槛没有配置");
        }
        if(raffleCount >= Integer.parseInt(ruleValue)){
            //大于阈值放行
            return DefaultRuleFilterTreeFactory.Result.builder()
                    .state(RuleFilterStateVO.ALLOW)
                    .data(DefaultRuleFilterTreeFactory.ResultData.builder()
                            .awardId(ruleFilterParamEntity.getAwardId())
                            .build())
                    .build();
        }
        log.info("用户:{} 抽奖次数不足", ruleFilterParamEntity.getUserId());
        return DefaultRuleFilterTreeFactory.Result.builder()
                .state(RuleFilterStateVO.TAKE_OVER)
                .data(DefaultRuleFilterTreeFactory.ResultData.builder()
                        .build())
                .build();
    }

    @Override
    protected String ruleModel() {
        return "rule_lock";
    }

}
