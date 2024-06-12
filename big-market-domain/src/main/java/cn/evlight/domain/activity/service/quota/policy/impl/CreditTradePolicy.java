package cn.evlight.domain.activity.service.quota.policy.impl;

import cn.evlight.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import cn.evlight.domain.activity.model.valobj.OrderStateVO;
import cn.evlight.domain.activity.repository.IActivityRepository;
import cn.evlight.domain.activity.service.quota.policy.ITradePolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description: 积分支付交易类型实现类
 * @Author: evlight
 * @Date: 2024/6/12
 */

@Slf4j
@Component("credit_pay_trade")
public class CreditTradePolicy implements ITradePolicy {

    @Autowired
    private IActivityRepository activityRepository;

    @Override
    public void trade(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        createQuotaOrderAggregate.getActivityOrderEntity().setState(OrderStateVO.wait_pay);
        activityRepository.CreditTradeOrder(createQuotaOrderAggregate);
    }

}
