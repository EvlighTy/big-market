package cn.evlight.domain.activity.service;

import cn.evlight.domain.activity.model.entity.ActivityCountEntity;
import cn.evlight.domain.activity.model.entity.ActivityEntity;
import cn.evlight.domain.activity.model.entity.ActivitySkuEntity;
import cn.evlight.domain.activity.repository.IActivityRepository;
import cn.evlight.domain.activity.service.chain.factory.DefaultCheckChainFactory;

/**
 * @Description: 抽奖活动基础功能支持类
 * @Author: evlight
 * @Date: 2024/6/2
 */
public class RaffleActivitySupport {

    protected DefaultCheckChainFactory defaultCheckChainFactory;

    protected IActivityRepository activityRepository;

    public RaffleActivitySupport(IActivityRepository activityRepository, DefaultCheckChainFactory defaultCheckChainFactory) {
        this.activityRepository = activityRepository;
        this.defaultCheckChainFactory = defaultCheckChainFactory;
    }

    public ActivitySkuEntity queryActivitySku(Long sku) {
        return activityRepository.queryActivitySku(sku);
    }

    public ActivityEntity queryRaffleActivityByActivityId(Long activityId) {
        return activityRepository.queryRaffleActivityByActivityId(activityId);
    }

    public ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId) {
        return activityRepository.queryRaffleActivityCountByActivityCountId(activityCountId);
    }

}
