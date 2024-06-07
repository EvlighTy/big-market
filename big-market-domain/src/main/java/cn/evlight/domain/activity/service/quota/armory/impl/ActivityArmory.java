package cn.evlight.domain.activity.service.quota.armory.impl;

import cn.evlight.domain.activity.model.entity.ActivitySkuEntity;
import cn.evlight.domain.activity.repository.IActivityRepository;
import cn.evlight.domain.activity.service.quota.armory.IActivityArmory;
import cn.evlight.domain.activity.service.quota.armory.IActivityDispatch;
import cn.evlight.types.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @Description: 活动预热装配实现类
 * @Author: evlight
 * @Date: 2024/6/4
 */

@Component
public class ActivityArmory implements IActivityArmory, IActivityDispatch {

    @Autowired
    private IActivityRepository activityRepository;

    @Override
    public boolean assembleActivitySkuBySku(Long sku) {
        //查询活动SKU信息
        ActivitySkuEntity activitySkuEntity = activityRepository.queryActivitySkuBySku(sku);
        //缓存活动信息
        activityRepository.queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());
        //缓存活动限制次数
        activityRepository.queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());
        //缓存活动库存
        cacheActivitySkuStockCount(sku, activitySkuEntity.getStockCount());
        return false;
    }

    @Override
    public boolean assembleActivitySkuByActivityId(Long activityId) {
        List<ActivitySkuEntity> activitySkuEntities = activityRepository.queryActivitySkuByActivityId(activityId);
        for (ActivitySkuEntity activitySkuEntity : activitySkuEntities) {
            //缓存活动库存
            cacheActivitySkuStockCount(activitySkuEntity.getSku(), activitySkuEntity.getStockCount());
            //缓存活动限制次数
            activityRepository.queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());
        }
        //缓存活动信息
        activityRepository.queryRaffleActivityByActivityId(activityId);
        return true;
    }

    private void cacheActivitySkuStockCount(Long sku, Integer stockCount) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_KEY + sku;
        activityRepository.cacheActivitySkuStockCount(cacheKey, stockCount);
    }

    @Override
    public boolean subtractActivitySkuStockCount(Long sku, Date endDate) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_KEY + sku;
        return activityRepository.subtractActivitySkuStockCount(cacheKey, sku, endDate);
    }
}
