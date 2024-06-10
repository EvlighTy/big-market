package cn.evlight.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import cn.evlight.infrastructure.persistent.po.UserBehaviorRebateOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 用户行为返利流水订单表 Mapper 接口
 * </p>
 *
 * @author evlight
 * @since 2024-06-09
 */
@Mapper
@DBRouterStrategy(splitTable = true)
public interface UserBehaviorRebateOrderMapper extends BaseMapper<UserBehaviorRebateOrder> {

    void saveBatch(ArrayList<UserBehaviorRebateOrder> userBehaviorRebateOrders);

    List<UserBehaviorRebateOrder> getUserBehaviorRebateOrderByOutBizId(UserBehaviorRebateOrder userBehaviorRebateOrder);
}
