package cn.evlight.domain.activity.service.chain.impl;

import cn.evlight.domain.activity.model.entity.ActivityCountEntity;
import cn.evlight.domain.activity.model.entity.ActivityEntity;
import cn.evlight.domain.activity.model.entity.ActivitySkuEntity;
import cn.evlight.domain.activity.service.chain.AbstractCheckChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Description: SKU库存校验
 * @Author: evlight
 * @Date: 2024/6/2
 */

@Slf4j
@Component("sku_check")
public class SkuStockCheck extends AbstractCheckChain {
    @Override
    public boolean doCheck(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {
        log.info("SKU库存校验...");
        return true;
    }
}
