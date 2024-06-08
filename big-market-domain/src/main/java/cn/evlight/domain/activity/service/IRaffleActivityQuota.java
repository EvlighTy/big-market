package cn.evlight.domain.activity.service;

import cn.evlight.domain.activity.model.entity.RaffleActivityQuotaEntity;

/**
 * @Description: 抽奖活动额度接口
 * @Author: evlight
 * @Date: 2024/6/1
 */
public interface IRaffleActivityQuota {

    /**
    * @Description: 创建抽奖次数流水单
    * @Param: [raffleActivityQuotaEntity]
    * @return:
    * @Date: 2024/6/8
    */
    String createQuotaOrder(RaffleActivityQuotaEntity raffleActivityQuotaEntity);

    /**
    * @Description: 查询用户今日累计抽奖次数
    * @Param: [activityId, userId]
    * @return:
    * @Date: 2024/6/8
    */
    Integer getUserRaffleCountToday(Long activityId, String userId);
}
