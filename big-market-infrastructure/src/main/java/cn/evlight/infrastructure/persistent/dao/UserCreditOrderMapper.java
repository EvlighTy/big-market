package cn.evlight.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import cn.evlight.infrastructure.persistent.po.UserCreditOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户积分订单记录 Mapper 接口
 * </p>
 *
 * @author evlight
 * @since 2024-06-11
 */
@Mapper
@DBRouterStrategy(splitTable = true)
public interface UserCreditOrderMapper extends BaseMapper<UserCreditOrder> {

    void save(UserCreditOrder userCreditOrder);
}
