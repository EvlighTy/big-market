package cn.evlight.domain.strategy.service.ruleFilter;

/**
 * @Description: 过滤器抽象类
 * @Author: evlight
 * @Date: 2024/5/27
 */
public abstract class AbstractRuleFilter<T> implements IRuleFilter<T>{

    protected abstract String ruleModel();

}
