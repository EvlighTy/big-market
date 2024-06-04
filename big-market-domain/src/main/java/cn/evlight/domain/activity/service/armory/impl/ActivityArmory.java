package cn.evlight.domain.activity.service.armory.impl;

import cn.evlight.domain.activity.model.entity.ActivitySkuEntity;
import cn.evlight.domain.activity.repository.IActivityRepository;
import cn.evlight.domain.activity.service.armory.IActivityArmory;
import cn.evlight.domain.activity.service.armory.IActivityDispatch;
import cn.evlight.types.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

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
    public boolean assembleActivitySku(Long sku) {
        //查询活动SKU信息
        ActivitySkuEntity activitySkuEntity = activityRepository.queryActivitySku(sku);
        //缓存商品总库存
        cacheActivitySkuStockCount(sku, activitySkuEntity.getStockCount());
        return false;
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
