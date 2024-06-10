package cn.evlight.trigger.http;

import cn.evlight.api.IRaffleActivityService;
import cn.evlight.api.dto.request.AccountQuotaRequestDTO;
import cn.evlight.api.dto.request.RaffleActivityRequestDTO;
import cn.evlight.api.dto.response.AccountQuotaResponseDTO;
import cn.evlight.api.dto.response.RaffleActivityResponseDTO;
import cn.evlight.domain.activity.model.entity.RaffleActivityAccountEntity;
import cn.evlight.domain.activity.model.entity.RaffleActivityPartakeEntity;
import cn.evlight.domain.activity.model.entity.UserRaffleOrderEntity;
import cn.evlight.domain.activity.service.IRaffleActivityPartake;
import cn.evlight.domain.activity.service.IRaffleActivityQuota;
import cn.evlight.domain.activity.service.quota.armory.IActivityArmory;
import cn.evlight.domain.award.model.entity.UserAwardRecordEntity;
import cn.evlight.domain.award.model.valobj.AwardStateVO;
import cn.evlight.domain.award.service.IAwardService;
import cn.evlight.domain.rebate.model.entity.BehaviorEntity;
import cn.evlight.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import cn.evlight.domain.rebate.model.valobj.BehaviorTypeVO;
import cn.evlight.domain.rebate.service.IBehaviorRebateService;
import cn.evlight.domain.strategy.model.entity.RaffleParamEntity;
import cn.evlight.domain.strategy.model.entity.RaffleResultEntity;
import cn.evlight.domain.strategy.service.IRaffleStrategy;
import cn.evlight.domain.strategy.service.armory.IManagerStrategyArmory;
import cn.evlight.types.common.Constants;
import cn.evlight.types.exception.AppException;
import cn.evlight.types.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @Description: 活动抽奖服务api
 * @Author: evlight
 * @Date: 2024/6/7
 */

@Slf4j
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/activity/")
public class RaffleActivityController implements IRaffleActivityService {

    @Autowired
    private IManagerStrategyArmory managerStrategyArmory;

    @Autowired
    private IActivityArmory activityArmory;

    @Autowired
    private IRaffleStrategy raffleStrategy;

    @Autowired
    private IRaffleActivityQuota raffleActivityQuota;

    @Autowired
    private IRaffleActivityPartake raffleActivityPartake;

    @Autowired
    private IAwardService awardService;

    @Autowired
    private IBehaviorRebateService behaviorRebateService;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @GetMapping("/armory")
    @Override
    public Response<Boolean> assembleRaffleActivityArmory(@RequestParam Long activityId) {
        log.info("[request]-[活动抽奖装配]");
        try {
            //装配策略
            managerStrategyArmory.assembleRaffleStrategyByActivityId(activityId);
            //装配活动
            activityArmory.assembleActivitySkuByActivityId(activityId);
            log.info("[request]-[活动抽奖装配] 成功");
            return Response.success();
        } catch (AppException e) {
            log.error("[request]-[活动抽奖装配] 失败:{}",e.getMessage());
        }catch (Exception e){
            log.error("[request]-[活动抽奖装配] 失败:未知错误");
            e.printStackTrace();
        }
        return Response.error();
    }

    @PostMapping("/raffle")
    @Override
    public Response<RaffleActivityResponseDTO> activityRaffle(@RequestBody RaffleActivityRequestDTO request) {
        log.info("[request]-[活动抽奖]");
        try {
            //参数校验
            Long activityId = request.getActivityId();
            String userId = request.getUserId();
            if(activityId == null || StringUtils.isBlank(userId)){
                throw new AppException(Constants.ExceptionInfo.INVALID_PARAMS);
            }
            //创建抽奖单
            UserRaffleOrderEntity userRaffleOrderEntity = raffleActivityPartake.createPartakeOrder(RaffleActivityPartakeEntity.builder()
                    .activityId(activityId)
                    .userId(userId)
                    .build());
            //执行抽奖
            RaffleResultEntity raffleResultEntity = raffleStrategy.doRaffle(RaffleParamEntity.builder()
                    .strategyId(userRaffleOrderEntity.getStrategyId())
                    .userId(userId)
                    .endDataTime(userRaffleOrderEntity.getEndDateTime())
                    .build());
            //保存中奖记录
            awardService.saveUserAwardRecord(UserAwardRecordEntity.builder()
                            .activityId(activityId)
                            .strategyId(userRaffleOrderEntity.getStrategyId())
                            .userId(userId)
                            .orderId(userRaffleOrderEntity.getOrderId())
                            .awardId(raffleResultEntity.getAwardId())
                            .awardTitle(raffleResultEntity.getAwardTitle())
                            .awardTime(LocalDateTime.now())
                            .awardState(AwardStateVO.create)
                    .build());
            log.info("[request]-[活动抽奖] 成功");
            //返回结果
            return Response.success(RaffleActivityResponseDTO.builder()
                            .awardId(raffleResultEntity.getAwardId())
                            .awardTitle(raffleResultEntity.getAwardTitle())
                            .awardIndex(raffleResultEntity.getSort())
                    .build());
        } catch (AppException e) {
            log.error("[request]-[活动抽奖] 失败:{}", e.getCode());
        } catch (Exception e){
            log.error("[request]-[活动抽奖] 失败:未知错误");
            e.printStackTrace();
        }
        return Response.error();
    }

    @PostMapping("/sign_rebate")
    @Override
    public Response<Boolean> signInRebate(@RequestParam String userId) {
        try {
            log.info("[request]-[行为返利]");
            List<String> orderIds = behaviorRebateService.createOrder(BehaviorEntity.builder()
                    .userId(userId)
                    .outBizId(dateTimeFormatter.format(LocalDateTime.now()))
                    .behaviorTypeVO(BehaviorTypeVO.SIGN)
                    .build());
            log.info("[request]-[行为返利] 成功 结果:{}", orderIds);
            return Response.success(Boolean.TRUE);
        } catch (AppException e){
            log.info("[request]-[行为返利] 失败:{}", e.getCode());
        } catch (Exception e){
            log.info("[request]-[行为返利] 失败:未知错误");
        }
        return Response.error(Boolean.FALSE);
    }

    @PostMapping("/is_sign_or_not")
    @Override
    public Response<Boolean> isSignOrNot(@RequestParam String userId) {
        try {
            log.info("[request]-[查询用户今日是否已签到]");
            String outBizId = dateTimeFormatter.format(LocalDateTime.now());
            List<UserBehaviorRebateOrderEntity> userBehaviorRebateOrderEntities = behaviorRebateService.getUserBehaviorRebateOrderEntityByOutBizId(userId, outBizId);
            log.info("[request]-[查询用户今日是否已签到] 结果:{}", !userBehaviorRebateOrderEntities.isEmpty());
            return Response.success(userBehaviorRebateOrderEntities.isEmpty());
        } catch (AppException e){
            log.info("[request]-[查询用户今日是否已签到] 失败:{}", e.getCode());
        } catch (Exception e){
            log.info("[request]-[查询用户今日是否已签到] 失败:未知错误");
        }
        return Response.error(Boolean.FALSE);
    }

    @PostMapping("/account_quota")
    @Override
    public Response<AccountQuotaResponseDTO> getUserAccountQuota(@RequestBody AccountQuotaRequestDTO request) {
        try {
            log.info("[request]-[查询用户总额度账户]");
            //参数校验
            Long activityId = request.getActivityId();
            String userId = request.getUserId();
            if(activityId == null || StringUtils.isBlank(userId)){
                return Response.error(Constants.Exception.invalid_params);
            }
            RaffleActivityAccountEntity raffleActivityAccount = raffleActivityQuota.getUserAccountQuota(activityId, userId);
            AccountQuotaResponseDTO accountQuotaResponseDTO = AccountQuotaResponseDTO.builder()
                    .totalCount(raffleActivityAccount.getTotalCount())
                    .totalCountSurplus(raffleActivityAccount.getTotalCountSurplus())
                    .dayCount(raffleActivityAccount.getDayCount())
                    .dayCountSurplus(raffleActivityAccount.getDayCountSurplus())
                    .monthCount(raffleActivityAccount.getMonthCount())
                    .monthCountSurplus(raffleActivityAccount.getMonthCountSurplus())
                    .build();
            log.info("[request]-[查询用户总额度账户] 成功");
            return Response.success(accountQuotaResponseDTO);
        } catch (AppException e){
            log.info("[request]-[查询用户总额度账户] 失败:{}", e.getCode());
        } catch (Exception e){
            log.info("[request]-[查询用户总额度账户] 失败:未知错误");
        }
        return Response.error();
    }

    /*
    log.info("[request]-[]");
    try {
        log.info("[request]-[] 成功");
        return Response.success();
    } catch (AppException e){
        log.info("[request]-[] 失败:{}", e.getCode());
    } catch (Exception e){
        log.info("[request]-[] 失败:未知错误");
    }
    return Response.error();
    */

}
