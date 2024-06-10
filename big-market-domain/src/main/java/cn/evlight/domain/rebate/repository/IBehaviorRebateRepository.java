package cn.evlight.domain.rebate.repository;

import cn.evlight.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import cn.evlight.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import cn.evlight.domain.rebate.model.valobj.BehaviorTypeVO;
import cn.evlight.domain.rebate.model.valobj.DailyBehaviorRebateVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: evlight
 * @Date: 2024/6/9
 */
public interface IBehaviorRebateRepository {

    /**
    * @Description: 查询日常行为返利配置
    * @Param: [behaviorTypeVO]
    * @return:
    * @Date: 2024/6/9
    */
    List<DailyBehaviorRebateVO> getDailyBehaviorRebateConfig(BehaviorTypeVO behaviorTypeVO);

    /**
    * @Description: 保存行为返利订单记录，发送消息到MQ
    * @Param: [behaviorRebateAggregates]
    * @return:
    * @Date: 2024/6/9
    */
    void saveUserBehaviorRebateOrder(ArrayList<BehaviorRebateAggregate> behaviorRebateAggregates);

    /**
     * table: user_behavior_rebate_order
    * @Description: 根据业务防重ID查询用户行为返利订单记录
    * @Param: [userId, outBizId]
    * @return:
    * @Date: 2024/6/10
    */
    List<UserBehaviorRebateOrderEntity> getUserBehaviorRebateOrderEntityByOutBizId(String userId, String outBizId);
}
