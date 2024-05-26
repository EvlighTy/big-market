package cn.evlight.domain.strategy.service;

import cn.evlight.domain.strategy.model.entity.RaffleParamEntity;
import cn.evlight.domain.strategy.model.entity.RaffleResultEntity;

/**
 * @Description: 抽奖策略接口
 * @Author: evlight
 * @Date: 2024/5/25
 */
public interface IRaffleStrategy {

    /**
    * @Description: 执行抽奖
    * @Param: [raffleParamEntity] 抽奖所需参数
    * @return: 抽奖结果实体
    * @Date: 2024/5/25
    */
    RaffleResultEntity doRaffle(RaffleParamEntity raffleParamEntity);

}
