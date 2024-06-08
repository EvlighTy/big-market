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
    * @Description: 查询策略奖品集合
    * @Param: [strategyId] 策略ID
    * @return:
    * @Date: 2024/5/29
    */
    List<StrategyAwardEntity> getStrategyAwardList(Long strategyId);

    /**
    * @Description: 查询策略奖品集合
    * @Param: [activityId]
    * @return:
    * @Date: 2024/6/8
    */
    List<StrategyAwardEntity> getStrategyAwardListByActivityId(Long activityId);

}
