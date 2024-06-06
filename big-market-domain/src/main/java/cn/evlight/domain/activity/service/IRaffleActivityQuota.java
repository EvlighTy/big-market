package cn.evlight.domain.activity.service;

import cn.evlight.domain.activity.model.entity.RaffleActivityQuotaEntity;

/**
 * @Description: 抽奖活动额度接口
 * @Author: evlight
 * @Date: 2024/6/1
 */
public interface IRaffleActivityQuota {

    String createQuotaOrder(RaffleActivityQuotaEntity raffleActivityQuotaEntity);

}
