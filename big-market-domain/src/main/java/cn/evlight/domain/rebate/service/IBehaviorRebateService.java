package cn.evlight.domain.rebate.service;

import cn.evlight.domain.rebate.model.entity.BehaviorEntity;

import java.util.List;

/**
 * @Description: 行为返利服务接口
 * @Author: evlight
 * @Date: 2024/6/9
 */
public interface IBehaviorRebateService {

    /**
    * @Description: 保存行为返利订单
    * @Param: [behaviorEntity]
    * @return:
    * @Date: 2024/6/9
    */
    List<String> createOrder(BehaviorEntity behaviorEntity);

}
