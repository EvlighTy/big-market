package cn.evlight.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.evlight.infrastructure.persistent.po.RaffleActivityAccountMonth;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 抽奖活动账户表-月次数 Mapper 接口
 * </p>
 *
 * @author evlight
 * @since 2024-06-06
 */
@Mapper
public interface RaffleActivityAccountMonthMapper extends BaseMapper<RaffleActivityAccountMonth> {

    @DBRouter
    RaffleActivityAccountMonth queryRaffleActivityAccountMonth(RaffleActivityAccountMonth raffleActivityAccountMonth);

    int raffleOrderConsume(RaffleActivityAccountMonth raffleActivityAccountMonth);

    void save(RaffleActivityAccountMonth raffleActivityAccountMonth);

    void addQuota(RaffleActivityAccountMonth raffleActivityAccountMonth);

    @DBRouter
    RaffleActivityAccountMonth getUserAccountQuota(RaffleActivityAccountMonth raffleActivityAccountMonth);
}
