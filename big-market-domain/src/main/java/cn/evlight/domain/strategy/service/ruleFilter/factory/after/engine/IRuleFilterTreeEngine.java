package cn.evlight.domain.strategy.service.ruleFilter.factory.after.engine;

import cn.evlight.domain.strategy.model.entity.RuleFilterParamEntity;
import cn.evlight.domain.strategy.service.ruleFilter.factory.after.DefaultRuleFilterTreeFactory;

/**
 * @Description: 过滤器规则树引擎接口
 * @Author: evlight
 * @Date: 2024/5/27
 */
public interface IRuleFilterTreeEngine {

    DefaultRuleFilterTreeFactory.ResultData process(RuleFilterParamEntity ruleFilterParamEntity);

}
