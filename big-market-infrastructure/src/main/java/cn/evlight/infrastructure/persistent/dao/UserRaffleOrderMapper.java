package cn.evlight.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import cn.evlight.infrastructure.persistent.po.UserRaffleOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户抽奖订单表 Mapper 接口
 * </p>
 *
 * @author evlight
 * @since 2024-06-06
 */
@Mapper
@DBRouterStrategy(splitTable = true)
public interface UserRaffleOrderMapper extends BaseMapper<UserRaffleOrder> {

    @DBRouter
    UserRaffleOrder queryUnUsedRaffleOrder(UserRaffleOrder userRaffleOrder);

    void save(UserRaffleOrder userRaffleOrder);

    int updateAfterRaffle(UserRaffleOrder userRaffleOrder);
}
