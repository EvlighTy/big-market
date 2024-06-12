package cn.evlight.domain.activity.service.quota.policy;

import cn.evlight.domain.activity.model.aggregate.CreateQuotaOrderAggregate;

/**
 * @Description: 交易类型接口
 * @Author: evlight
 * @Date: 2024/6/12
 */
public interface ITradePolicy {

    void trade(CreateQuotaOrderAggregate createQuotaOrderAggregate);

}
