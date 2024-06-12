package cn.evlight.domain.credit.repository;

import cn.evlight.domain.credit.model.aggregate.CreditAggregate;

/**
 * @Description: 积分仓库接口
 * @Author: evlight
 * @Date: 2024/6/11
 */
public interface ICreditRepository {

    /**
    * @Description: 保存积分账户聚合对象
    * @Param: [creditAggregate]
    * @return:
    * @Date: 2024/6/12
    */
    void saveCreditAggregate(CreditAggregate creditAggregate);

}
