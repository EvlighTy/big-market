package cn.evlight.domain.activity.repository;

import cn.evlight.domain.activity.model.aggregate.CreateOrderAggregate;
import cn.evlight.domain.activity.model.entity.ActivityCountEntity;
import cn.evlight.domain.activity.model.entity.ActivityEntity;
import cn.evlight.domain.activity.model.entity.ActivitySkuEntity;

/**
 * @Description: 活动仓储接口
 * @Author: evlight
 * @Date: 2024/6/1
 */
public interface IActivityRepository {

    /**
    * @Description: 查询活动SKU
    * @Param: [sku] sku编号
    * @return:
    * @Date: 2024/6/2
    */
    ActivitySkuEntity queryActivitySku(Long sku);

    /**
    * @Description: 查询抽奖活动实体
    * @Param: [activityId] 活动ID
    * @return:
    * @Date: 2024/6/2
    */
    ActivityEntity queryRaffleActivityByActivityId(Long activityId);

    /**
    * @Description: 查询抽奖活动次数实体
    * @Param: [activityCountId] 活动次数编号
    * @return:
    * @Date: 2024/6/2
    */
    ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId);

    /**
    * @Description: 保存订单
    * @Param: [createOrderAggregate]
    * @return:
    * @Date: 2024/6/2
    */
    void saveOrder(CreateOrderAggregate createOrderAggregate);
}
