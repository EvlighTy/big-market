package cn.evlight.api;

import cn.evlight.api.model.RaffleRequestDTO;
import cn.evlight.api.model.RaffleResponseDTO;
import cn.evlight.api.model.StrategyAwardListRequestDTO;
import cn.evlight.api.model.StrategyAwardListResponseDTO;
import cn.evlight.types.model.Response;

import java.util.List;

/**
 * @Description: 抽奖服务接口
 * @Author: evlight
 * @Date: 2024/5/29
 */
public interface IRaffleService {

    /**
    * @Description: 装配抽奖策略库
    * @Param: [strategyId] 策略ID
    * @return: 装配结果
    * @Date: 2024/5/29
    */
    Response<Boolean> assembleRaffleStrategyArmory(Long strategyId);

    /**
    * @Description: 获取策略奖品列表
    * @Param: [requestDTO]
    * @return:
    * @Date: 2024/5/29
    */
    Response<List<StrategyAwardListResponseDTO>> getStrategyAwardList(StrategyAwardListRequestDTO requestDTO);

    /**
    * @Description: 抽奖
    * @Param: [requestDTO]
    * @return:
    * @Date: 2024/5/29
    */
    Response<RaffleResponseDTO> raffle(RaffleRequestDTO requestDTO);

}
