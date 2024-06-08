package cn.evlight.domain.strategy.service;

import cn.evlight.domain.strategy.model.valobj.StrategyAwardStockKeyVO;

/**
 * @Description: 抽奖库存操作接口
 * @Author: evlight
 * @Date: 2024/5/29
 */
public interface IRaffleStock {

    /**
    * @Description: 获取奖品库存消耗延迟队列
    * @Param: []
    * @return:
    * @Date: 2024/6/4
    */
    StrategyAwardStockKeyVO takeQueueValue() throws InterruptedException;

    /**
    * @Description: 更新奖品库存
    * @Param: [strategyId, awardId]
    * @return:
    * @Date: 2024/6/4
    */
    void updateStrategyAwardStock(Long strategyId, Integer awardId);

}
