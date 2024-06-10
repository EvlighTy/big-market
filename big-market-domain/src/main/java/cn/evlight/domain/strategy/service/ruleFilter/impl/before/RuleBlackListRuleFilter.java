package cn.evlight.domain.strategy.service.ruleFilter.impl.before;

import cn.evlight.domain.strategy.model.entity.RuleFilterParamEntity;
import cn.evlight.domain.strategy.model.entity.StrategyRuleEntity;
import cn.evlight.domain.strategy.repository.IStrategyRepository;
import cn.evlight.domain.strategy.service.ruleFilter.AbstractBeforeRuleFilter;
import cn.evlight.domain.strategy.service.ruleFilter.factory.before.DefaultRuleFilterChainFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * @Description: 黑名单过滤器
 * @Author: evlight
 * @Date: 2024/5/26
 */

@Slf4j
@Component("rule_blacklist")
public class RuleBlackListRuleFilter extends AbstractBeforeRuleFilter {

    @Autowired
    private IStrategyRepository strategyRepository;

    @Override
    public DefaultRuleFilterChainFactory.ResultData doFilter(RuleFilterParamEntity ruleFilterParamEntity) {
        log.info("黑名单过滤...");
        //查询黑名单
        StrategyRuleEntity strategyRuleEntity = strategyRepository.getStrategyRuleEntity(ruleFilterParamEntity.getStrategyId(), ruleModel());
        HashMap<Integer, List<String>> ruleBlacklistValues = strategyRuleEntity.getRuleBlacklistValues();
        //过滤黑名单用户
        for (Integer key : ruleBlacklistValues.keySet()) {
            List<String> blacklist = ruleBlacklistValues.get(key);
            if (blacklist.contains(ruleFilterParamEntity.getUserId())){
                //是黑名单用户
                log.info("黑名单用户被拦截:{}", ruleFilterParamEntity.getUserId());
                return DefaultRuleFilterChainFactory.ResultData.builder()
                        .awardId(key)
                        .ruleModel(ruleModel())
                        .build();
            }
        }
        //放行
        return next().doFilter(ruleFilterParamEntity);
    }

    @Override
    protected String ruleModel() {
        return DefaultRuleFilterChainFactory.RuleModel.RULE_BLACKLIST.getCode();
    }

}
