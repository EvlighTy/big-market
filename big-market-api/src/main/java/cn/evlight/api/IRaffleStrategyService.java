package cn.evlight.api;

import cn.evlight.api.dto.request.RaffleStrategyAwardListRequestDTO;
import cn.evlight.api.dto.request.RaffleStrategyRequestDTO;
import cn.evlight.api.dto.request.StrategyRuleWeightRequestDTO;
import cn.evlight.api.dto.response.RaffleStrategyAwardListResponseDTO;
import cn.evlight.api.dto.response.RaffleStrategyResponseDTO;
import cn.evlight.api.dto.response.StrategyRuleWeightResponseDTO;
import cn.evlight.types.model.Response;

import java.util.List;

/**
 * @Description: 抽奖服务接口
 * @Author: evlight
 * @Date: 2024/5/29
 */
public interface IRaffleStrategyService {

    /**
    * @Description: 装配抽奖策略库, 策略预热
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
    Response<List<RaffleStrategyAwardListResponseDTO>> getStrategyAwardList(RaffleStrategyAwardListRequestDTO request);

    /**
    * @Description: 抽奖
    * @Param: [requestDTO]
    * @return:
    * @Date: 2024/5/29
    */
    Response<RaffleStrategyResponseDTO> strategyRaffle(RaffleStrategyRequestDTO request);

    /**
    * @Description: 查询策略权重抽奖规则的值
    * @Param: [request]
    * @return:
    * @Date: 2024/6/10
    */
    Response<List<StrategyRuleWeightResponseDTO>> getStrategyRuleWeight(StrategyRuleWeightRequestDTO request);

}
