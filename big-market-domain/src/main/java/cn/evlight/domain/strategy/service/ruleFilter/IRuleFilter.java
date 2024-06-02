package cn.evlight.domain.strategy.service.ruleFilter;

import cn.evlight.domain.strategy.model.entity.RuleFilterParamEntity;

/**
 * @Description: 规则过滤接口
 * @Author: evlight
 * @Date: 2024/5/25
 */
public interface IRuleFilter<T> {

    T doFilter(RuleFilterParamEntity ruleFilterParamEntity);

}
