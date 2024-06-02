package cn.evlight.domain.activity.service.chain;

import cn.evlight.domain.activity.model.entity.ActivityCountEntity;
import cn.evlight.domain.activity.model.entity.ActivityEntity;
import cn.evlight.domain.activity.model.entity.ActivitySkuEntity;

/**
 * @Description: 校验链接口
 * @Author: evlight
 * @Date: 2024/6/2
 */
public interface ICheckChain extends ICheckChainArmory{

    boolean doCheck(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity);

}
