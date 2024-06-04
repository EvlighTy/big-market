package cn.evlight.domain.activity.repository;

import cn.evlight.domain.activity.model.aggregate.CreateOrderAggregate;
import cn.evlight.domain.activity.model.entity.ActivityCountEntity;
import cn.evlight.domain.activity.model.entity.ActivityEntity;
import cn.evlight.domain.activity.model.entity.ActivitySkuEntity;
import cn.evlight.domain.activity.model.valobj.ActivitySkuStockKeyVO;

import java.util.Date;

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

    /**
    * @Description: 缓存商品库存
    * @Param: [cacheKey, stockCount]
    * @return:
    * @Date: 2024/6/4
    */
    boolean cacheActivitySkuStockCount(String cacheKey, Integer stockCount);

    /**
    * @Description: 尝试扣减商品剩余库存
    * @Param: [cacheKey, sku, endDate]
    * @return:
    * @Date: 2024/6/4
    */
    boolean subtractActivitySkuStockCount(String cacheKey, Long sku, Date endDate);

    /**
    * @Description: 发送消息到延迟队列延迟更新商品库存
    * @Param: [activitySkuStockKeyVO]
    * @return:
    * @Date: 2024/6/4
    */
    void sendToActivityStockConsumeQueue(ActivitySkuStockKeyVO activitySkuStockKeyVO);

    /**
    * @Description: 获取sku库存更新延迟队列
    * @Param: []
    * @return:
    * @Date: 2024/6/4
    */
    ActivitySkuStockKeyVO takeQueueValue();

    /**
    * @Description: 清空sku库存更新延迟队列
    * @Param: []
    * @return:
    * @Date: 2024/6/4
    */
    void clearQueueValue();

    /**
    * @Description: 更新数据库sku库存
    * @Param: [sku]
    * @return:
    * @Date: 2024/6/4
    */
    void updateActivitySkuStock(Long sku);

    /**
    * @Description: 清空数据库sku库存
    * @Param: [sku]
    * @return:
    * @Date: 2024/6/4
    */
    void clearActivitySkuStock(Long sku);
}
