package cn.evlight.domain.strategy.service.rule.filter;

import cn.evlight.domain.strategy.service.rule.filter.factory.before.DefaultRuleFilterChainFactory;

/**
 * @Description: 前置过滤器抽象类
 * @Author: evlight
 * @Date: 2024/5/27
 */
public abstract class AbstractBeforeRuleFilter extends AbstractRuleFilter<DefaultRuleFilterChainFactory.ResultData> {

    AbstractBeforeRuleFilter next;

    public AbstractBeforeRuleFilter next() {
        return next;
    }

    public AbstractBeforeRuleFilter addNext(AbstractBeforeRuleFilter next) {
        this.next = next;
        return next;
    }


}
