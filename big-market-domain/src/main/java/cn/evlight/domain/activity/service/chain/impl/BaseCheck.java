package cn.evlight.domain.activity.service.chain.impl;

import cn.evlight.domain.activity.model.entity.ActivityCountEntity;
import cn.evlight.domain.activity.model.entity.ActivityEntity;
import cn.evlight.domain.activity.model.entity.ActivitySkuEntity;
import cn.evlight.domain.activity.model.valobj.ActivityStateVO;
import cn.evlight.domain.activity.service.chain.AbstractCheckChain;
import cn.evlight.types.common.Constants;
import cn.evlight.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Description: 基础校验
 * @Author: evlight
 * @Date: 2024/6/2
 */

@Slf4j
@Component("base_check")
public class BaseCheck extends AbstractCheckChain {

    @Override
    public boolean doCheck(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {
        log.info("基础校验...");
        if(!activityEntity.getState().equals(ActivityStateVO.open)){
            //活动未开启
            throw new AppException(Constants.ExceptionInfo.INVALID_ACTIVITY_STATE);
        }
        Date currentDate = new Date();
        if(activityEntity.getBeginDateTime().after(currentDate) || activityEntity.getEndDateTime().before(currentDate)){
            //当前日期未在活动时间范围内
            throw new AppException(Constants.ExceptionInfo.INVALID_ACTIVITY_DATE);
        }
        if(activitySkuEntity.getStockCountSurplus() <= 0){
            //库存不足
            throw new AppException(Constants.ExceptionInfo.ACTIVITY_STOCK_INSUFFICIENT);
        }
        //放行
        return next().doCheck(activitySkuEntity, activityEntity, activityCountEntity);
    }
}
