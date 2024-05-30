package cn.evlight.domain.strategy.service;

import cn.evlight.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;

/**
 * @Description: 抽奖奖品接口
 * @Author: evlight
 * @Date: 2024/5/29
 */
public interface IRaffleAward {

    /**
    * @Description: 查询策略奖品列表
    * @Param: [strategyId] 策略ID
    * @return:
    * @Date: 2024/5/29
    */
    List<StrategyAwardEntity> getStrategyAwardList(Long strategyId);

}
