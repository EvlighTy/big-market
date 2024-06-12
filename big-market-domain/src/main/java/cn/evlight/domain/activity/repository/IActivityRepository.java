package cn.evlight.domain.activity.repository;

import cn.evlight.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import cn.evlight.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import cn.evlight.domain.activity.model.entity.*;
import cn.evlight.domain.activity.model.valobj.ActivitySkuStockKeyVO;

import java.time.LocalDateTime;
import java.util.List;

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
    ActivitySkuEntity queryActivitySkuBySku(Long sku);

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
     * table: raffle_activity_order, raffle_activity_account
    * @Description: 保存额度订单
    * @Param: [createQuotaOrderAggregate]
    * @return:
    * @Date: 2024/6/2
    */
//    void saveOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate);

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
    boolean subtractActivitySkuStock(String cacheKey, Long sku, LocalDateTime endDate);

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
    void clearQueueValue(Long sku);

    /**
     * table: raffle_activity_sku
    * @Description: 更新数据库sku库存
    * @Param: [sku]
    * @return:
    * @Date: 2024/6/4
    */
    void updateActivitySkuStock(Long sku);

    /**
     * table: raffle_activity_sku
    * @Description: 清空数据库sku库存
    * @Param: [sku]
    * @return:
    * @Date: 2024/6/4
    */
    void clearActivitySkuStock(Long sku);

    /**
     * table: user_raffle_order
    * @Description: 查询未使用的抽奖单
    * @Param: [activityId, userId]
    * @return:
    * @Date: 2024/6/6
    */
    UserRaffleOrderEntity queryUnUsedRaffleOrder(Long activityId, String userId);

    /**
     * table: raffle_activity_account
    * @Description: 查询用户总额度
    * @Param: [activityId, userId]
    * @return:
    * @Date: 2024/6/6
    */
    RaffleActivityAccountEntity queryRaffleActivityAccount(Long activityId, String userId);

    /**
     * table: raffle_activity_account_month
    * @Description: 查询用户月额度
    * @Param: [activityId, userId, monthDateTime]
    * @return:
    * @Date: 2024/6/6
    */
    RaffleActivityAccountMonthEntity queryRaffleActivityAccountMonth(Long activityId, String userId, String monthDateTime);

    /**
     * table: raffle_activity_account_day
    * @Description: 查询用户日额度
    * @Param: [activityId, userId, dayDateTime]
    * @return:
    * @Date: 2024/6/6
    */
    RaffleActivityAccountDayEntity queryRaffleActivityAccountDay(Long activityId, String userId, String dayDateTime);

    /**
     * table: raffle_activity_account, raffle_activity_account_month, raffle_activity_account_day, raffle_activity_order
    * @Description: 保存抽奖活动参与聚合对象
    * @Param: [createPartakeOrderAggregate]
    * @return:
    * @Date: 2024/6/6
    */
    void savePartakeOrderAggregate(CreatePartakeOrderAggregate createPartakeOrderAggregate);

    /**
    * @Description: 查询活动SKU信息
    * @Param: [activityId]
    * @return:
    * @Date: 2024/6/7
    */
    List<ActivitySkuEntity> queryActivitySkuByActivityId(Long activityId);

    /**
    * @Description: 查询用户今日累计抽奖次数
    * @Param: [activityId, userId]
    * @return:
    * @Date: 2024/6/8
    */
    Integer getUserRaffleCountToday(Long activityId, String userId);

    /**
    * @Description: 判断库存是否已清空
    * @Param: [cacheKey]
    * @return:
    * @Date: 2024/6/8
    */
    boolean SkuStockIsZero(String cacheKey);

    /**
     * table: raffle_activity_account
    * @Description: 查询用户总额度账户
    * @Param: [activityId, userId]
    * @return:
    * @Date: 2024/6/10
    */
    RaffleActivityAccountEntity getUserAccountQuota(Long activityId, String userId);

    /**
    * @Description: 查询用户累计抽奖次数
    * @Param: [activityId, userId]
    * @return:
    * @Date: 2024/6/10
    */
    Integer getUserRaffleCount(Long activityId, String userId);

    /**
    * @Description: 保存积分支付类型的订单
    * @Param: [createQuotaOrderAggregate]
    * @return:
    * @Date: 2024/6/12
    */
    void CreditTradeOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate);

    /**
    * @Description: 保存返利支付类型的订单
    * @Param: [createQuotaOrderAggregate]
    * @return:
    * @Date: 2024/6/12
    */
    void saveRebateTradeOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate);

    /**
     * table: raffle_activity_account,
    * @Description: 用户积分兑换
    * @Param: [creditExchangeEntity]
    * @return:
    * @Date: 2024/6/12
    */
    void creditExchange(CreditExchangeEntity creditExchangeEntity);
}
