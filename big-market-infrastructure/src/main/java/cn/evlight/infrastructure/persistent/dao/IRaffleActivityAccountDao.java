package cn.evlight.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.evlight.infrastructure.persistent.po.RaffleActivityAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 抽奖活动账户表
 * @create 2024-03-09 10:05
 */
@Mapper
public interface IRaffleActivityAccountDao {

    void insert(RaffleActivityAccount raffleActivityAccount);

    int updateAccountQuota(RaffleActivityAccount raffleActivityAccount);

    @DBRouter
    RaffleActivityAccount queryRaffleActivityAccount(RaffleActivityAccount raffleActivityAccount);

    int raffleOrderConsume(RaffleActivityAccount raffleActivityAccount);

    void updateAccountMonth(RaffleActivityAccount raffleActivityAccount);

    void updateAccountDay(RaffleActivityAccount raffleActivityAccount);

    @DBRouter
    RaffleActivityAccount getUserAccountQuota(RaffleActivityAccount raffleActivityAccount);
}
