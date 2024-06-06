package cn.evlight.domain.activity.service.partake.chain;

import cn.evlight.domain.activity.model.entity.*;

/**
 * @Description: 校验链接口
 * @Author: evlight
 * @Date: 2024/6/2
 */
public interface IPartakeCheckChain extends IPartakeCheckChainArmory {

    boolean doCheck(ActivityEntity activityEntity);

}
