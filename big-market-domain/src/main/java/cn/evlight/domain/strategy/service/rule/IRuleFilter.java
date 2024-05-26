package cn.evlight.domain.strategy.service.rule;

import cn.evlight.domain.strategy.model.entity.RuleFilterParamEntity;
import cn.evlight.domain.strategy.model.entity.RuleFilterResultEntity;

/**
 * @Description: 规则过滤接口
 * @Author: evlight
 * @Date: 2024/5/25
 */
public interface IRuleFilter<T extends RuleFilterResultEntity.RuleFilterResult> {

    RuleFilterResultEntity<T> doFilter(RuleFilterParamEntity ruleFilterParamEntity);

}
