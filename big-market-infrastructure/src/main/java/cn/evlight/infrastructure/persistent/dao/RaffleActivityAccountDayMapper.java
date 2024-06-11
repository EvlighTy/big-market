package cn.evlight.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.evlight.infrastructure.persistent.po.RaffleActivityAccountDay;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 抽奖活动账户表-日次数 Mapper 接口
 * </p>
 *
 * @author evlight
 * @since 2024-06-06
 */
@Mapper
public interface RaffleActivityAccountDayMapper extends BaseMapper<RaffleActivityAccountDay> {

    @DBRouter
    RaffleActivityAccountDay queryRaffleActivityAccountDay(RaffleActivityAccountDay raffleActivityAccountDay);

    int raffleOrderConsume(RaffleActivityAccountDay raffleActivityAccountDay);

    void save(RaffleActivityAccountDay raffleActivityAccountDay);

    void addQuota(RaffleActivityAccountDay raffleActivityAccountDay);

    @DBRouter
    RaffleActivityAccountDay getUserAccountQuota(RaffleActivityAccountDay raffleActivityAccountDay);
}
