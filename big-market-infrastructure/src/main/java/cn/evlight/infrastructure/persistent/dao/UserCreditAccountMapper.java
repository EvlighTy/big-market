package cn.evlight.infrastructure.persistent.dao;

import cn.evlight.infrastructure.persistent.po.UserCreditAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户积分账户 Mapper 接口
 * </p>
 *
 * @author evlight
 * @since 2024-06-11
 */
@Mapper
public interface UserCreditAccountMapper extends BaseMapper<UserCreditAccount> {

    int addAmount(UserCreditAccount userCreditAccount);

    void save(UserCreditAccount userCreditAccount);
}
