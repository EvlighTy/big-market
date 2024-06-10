package cn.evlight.api;

import cn.evlight.api.dto.request.AccountQuotaRequestDTO;
import cn.evlight.api.dto.request.RaffleActivityRequestDTO;
import cn.evlight.api.dto.response.AccountQuotaResponseDTO;
import cn.evlight.api.dto.response.RaffleActivityResponseDTO;
import cn.evlight.types.model.Response;

/**
 * @Description: 抽奖活动服务api接口
 * @Author: evlight
 * @Date: 2024/6/7
 */
public interface IRaffleActivityService {

    /**
    * @Description: 活动预热
    * @Param: [activityId]
    * @return:
    * @Date: 2024/6/7
    */
    Response<Boolean> assembleRaffleActivityArmory(Long activityId);

    /**
    * @Description: 活动抽奖
    * @Param: [requestDTO]
    * @return:
    * @Date: 2024/6/7
    */
    Response<RaffleActivityResponseDTO> activityRaffle(RaffleActivityRequestDTO request);

    /**
    * @Description: 签到返利
    * @Param: [userId]
    * @return:
    * @Date: 2024/6/9
    */
    Response<Boolean> signInRebate(String userId);

    /**
    * @Description: 查询用户今日是否已签到
    * @Param: [userId]
    * @return:
    * @Date: 2024/6/10
    */
    Response<Boolean> isSignOrNot(String userId);

    /**
     * @Description: 查询用户总账户额度
     * @Param: [request]
     * @return:
     * @Date: 2024/6/10
     */
    Response<AccountQuotaResponseDTO> getUserAccountQuota(AccountQuotaRequestDTO request);

}
