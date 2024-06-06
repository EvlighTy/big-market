package cn.evlight.domain.activity.service.partake.chain.impl;

import cn.evlight.domain.activity.model.entity.*;
import cn.evlight.domain.activity.model.valobj.ActivityStateVO;
import cn.evlight.domain.activity.service.partake.chain.AbstractPartakeCheckChain;
import cn.evlight.types.common.Constants;
import cn.evlight.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Description: 基础校验(活动状态 、 活动有效日期)
 * @Author: evlight
 * @Date: 2024/6/6
 */

@Slf4j
@Component("partake_base_check")
public class BaseCheck extends AbstractPartakeCheckChain {

    @Override
    public boolean doCheck(ActivityEntity activityEntity) {
        log.info("基础校验");
        if(!activityEntity.getState().equals(ActivityStateVO.open)){
            //活动未开启
            throw new AppException(Constants.ExceptionInfo.INVALID_ACTIVITY_STATE);
        }
        Date currentDate = new Date();
        if(activityEntity.getBeginDateTime().after(currentDate) || activityEntity.getEndDateTime().before(currentDate)){
            //当前日期未在活动时间范围内
            throw new AppException(Constants.ExceptionInfo.INVALID_ACTIVITY_DATE);
        }
        //放行
        return true;
    }
}
