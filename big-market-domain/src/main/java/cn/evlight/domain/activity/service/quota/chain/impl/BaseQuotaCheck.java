package cn.evlight.domain.activity.service.quota.chain.impl;

import cn.evlight.domain.activity.model.entity.ActivityCountEntity;
import cn.evlight.domain.activity.model.entity.ActivityEntity;
import cn.evlight.domain.activity.model.entity.ActivitySkuEntity;
import cn.evlight.domain.activity.model.valobj.ActivityStateVO;
import cn.evlight.domain.activity.service.quota.chain.AbstractQuotaQuotaCheckChain;
import cn.evlight.types.common.Constants;
import cn.evlight.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Description: 基础校验
 * @Author: evlight
 * @Date: 2024/6/2
 */

@Slf4j
@Component("quota_base_check")
public class BaseQuotaCheck extends AbstractQuotaQuotaCheckChain {

    @Override
    public boolean doCheck(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {
        log.info("基础校验...");
        if(!activityEntity.getState().equals(ActivityStateVO.open)){
            //活动未开启
            throw new AppException(Constants.ExceptionInfo.INVALID_ACTIVITY_STATE);
        }
        LocalDateTime now = LocalDateTime.now();
        if(activityEntity.getBeginDateTime().isAfter(now) || activityEntity.getEndDateTime().isBefore(now)){
            //当前日期未在活动时间范围内
            throw new AppException(Constants.ExceptionInfo.INVALID_ACTIVITY_DATE);
        }
        if(activitySkuEntity.getStockCountSurplus() <= 0){
            //SKU库存不足
            throw new AppException(Constants.ExceptionInfo.ACTIVITY_STOCK_INSUFFICIENT);
        }
        //放行
        return next().doCheck(activitySkuEntity, activityEntity, activityCountEntity);
    }
}
