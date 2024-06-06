package cn.evlight.domain.activity.service.quota.chain.impl;

import cn.evlight.domain.activity.model.entity.ActivityCountEntity;
import cn.evlight.domain.activity.model.entity.ActivityEntity;
import cn.evlight.domain.activity.model.entity.ActivitySkuEntity;
import cn.evlight.domain.activity.model.valobj.ActivitySkuStockKeyVO;
import cn.evlight.domain.activity.repository.IActivityRepository;
import cn.evlight.domain.activity.service.quota.armory.IActivityDispatch;
import cn.evlight.domain.activity.service.quota.chain.AbstractQuotaQuotaCheckChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description: SKU库存校验
 * @Author: evlight
 * @Date: 2024/6/2
 */

@Slf4j
@Component("sku_check")
public class SkuStockCheck extends AbstractQuotaQuotaCheckChain {

    @Autowired
    private IActivityDispatch activityDispatch;

    @Autowired
    private IActivityRepository activityRepository;

    @Override
    public boolean doCheck(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {
        log.info("SKU库存扣减校验...");
        Long sku = activitySkuEntity.getSku();
        boolean success = activityDispatch.subtractActivitySkuStockCount(sku, activityEntity.getEndDateTime());
        if(success){
            log.info("库存扣减成功");
            //发送消息到延迟队列，延迟更新数据库库存
            activityRepository.sendToActivityStockConsumeQueue(ActivitySkuStockKeyVO.builder()
                            .sku(sku)
                            .activityId(activityEntity.getActivityId())
                    .build());
            return true;
        }
        log.info("库存扣减失败");
        return false;
    }

}
