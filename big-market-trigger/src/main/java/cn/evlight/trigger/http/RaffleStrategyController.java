package cn.evlight.trigger.http;

import cn.evlight.api.IRaffleStrategyService;
import cn.evlight.api.dto.request.RaffleStrategyAwardListRequestDTO;
import cn.evlight.api.dto.request.RaffleStrategyRequestDTO;
import cn.evlight.api.dto.request.StrategyRuleWeightRequestDTO;
import cn.evlight.api.dto.response.RaffleStrategyAwardListResponseDTO;
import cn.evlight.api.dto.response.RaffleStrategyResponseDTO;
import cn.evlight.api.dto.response.StrategyRuleWeightResponseDTO;
import cn.evlight.domain.activity.service.IRaffleActivityQuota;
import cn.evlight.domain.strategy.model.entity.RaffleParamEntity;
import cn.evlight.domain.strategy.model.entity.RaffleResultEntity;
import cn.evlight.domain.strategy.model.entity.StrategyAwardEntity;
import cn.evlight.domain.strategy.model.valobj.RuleWeightVO;
import cn.evlight.domain.strategy.service.IRaffleAward;
import cn.evlight.domain.strategy.service.IRaffleRule;
import cn.evlight.domain.strategy.service.IRaffleStrategy;
import cn.evlight.domain.strategy.service.armory.IManagerStrategyArmory;
import cn.evlight.domain.strategy.service.armory.IUserStrategyArmory;
import cn.evlight.types.common.Constants;
import cn.evlight.types.exception.AppException;
import cn.evlight.types.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 抽奖服务
 * @Author: evlight
 * @Date: 2024/5/29
 */

@Slf4j
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/strategy/")
public class RaffleStrategyController implements IRaffleStrategyService {

    @Autowired
    private IUserStrategyArmory userStrategyArmory;

    @Autowired
    private IManagerStrategyArmory managerStrategyArmory;

    @Autowired
    private IRaffleStrategy raffleStrategy;

    @Autowired
    private IRaffleAward raffleAward;

    @Autowired
    private IRaffleRule raffleRule;

    @Autowired
    private IRaffleActivityQuota raffleActivityQuota;

    @GetMapping("/armory")
    @Override
    public Response<Boolean> assembleRaffleStrategyArmory(@RequestParam Long strategyId) {
        log.info("[request]-[策略抽奖装配]");
        try {
            boolean result = managerStrategyArmory.assembleRaffleStrategy(strategyId);
            return Response.success(result);
        }catch (Exception e){
            e.printStackTrace();
            return Response.error();
        }
    }

    @PostMapping("/raffle_award_list")
    @Override
    public Response<List<RaffleStrategyAwardListResponseDTO>> getStrategyAwardList(@RequestBody RaffleStrategyAwardListRequestDTO request) {
        log.info("[request]-[查询策略奖品集合]");
        Long activityId = request.getActivityId();
        String userId = request.getUserId();
        //参数校验
        if(activityId == null || StringUtils.isBlank(userId)){
            return Response.error(Constants.ExceptionInfo.INVALID_PARAMS);
        }
        try {
            //查询策略奖品集合
            List<StrategyAwardEntity> strategyAwardList = raffleAward.getStrategyAwardListByActivityId(activityId);
            //查询奖品集合包含的规则
            String[] treeIds = strategyAwardList.stream()
                    .map(StrategyAwardEntity::getRuleModels)
                    .filter(ruleModels -> ruleModels != null && !StringUtils.isBlank(ruleModels))
                    .toArray(String[]::new);
            Map<String, Integer> awardRuleLockCountMap = raffleRule.getAwardRuleLockCount(treeIds);
            //查询用户今日累计抽奖次数
            Integer userRaffleCountToday = raffleActivityQuota.getUserRaffleCountToday(activityId, userId);
            ArrayList<RaffleStrategyAwardListResponseDTO> strategyAwardListResponseDTOS = new ArrayList<>();
            for (StrategyAwardEntity strategyAwardEntity : strategyAwardList) {
                //解锁阈值
                Integer ruleLockCount = awardRuleLockCountMap.get(strategyAwardEntity.getRuleModels());
                //是否解锁
                boolean isUnlocked = ruleLockCount == null || userRaffleCountToday >= ruleLockCount;
                RaffleStrategyAwardListResponseDTO strategyAwardListResponseDTO = RaffleStrategyAwardListResponseDTO.builder()
                                .awardId(strategyAwardEntity.getAwardId())
                                .awardTitle(strategyAwardEntity.getAwardTitle())
                                .awardSubtitle(strategyAwardEntity.getAwardSubtitle())
                                .awardRuleLockCount(ruleLockCount)
                                .isAwardUnlock(isUnlocked)
                                .waitUnLockCount(isUnlocked ? 0 : ruleLockCount - userRaffleCountToday)
                                .sort(strategyAwardEntity.getSort())
                                .build();
                strategyAwardListResponseDTOS.add(strategyAwardListResponseDTO);
            }
            log.info("[request]-[查询策略奖品集合] 成功:activityId:{} userId:{}", activityId, userId);
            return Response.success(strategyAwardListResponseDTOS);
        }catch (Exception e){
            e.printStackTrace();
            log.info("[request]-[查询策略奖品集合] 失败:activityId:{} userId:{}", activityId, userId);
            return Response.error();
        }
    }

    @PostMapping("/raffle")
    @Override
    public Response<RaffleStrategyResponseDTO> strategyRaffle(@RequestBody RaffleStrategyRequestDTO request) {
        log.info("[request]-{抽奖}");
        try {
            RaffleResultEntity result = raffleStrategy.doRaffle(RaffleParamEntity.builder()
                    .strategyId(request.getStrategyId())
                    .userId("123")
                    .build());
            return Response.success(RaffleStrategyResponseDTO.builder()
                    .awardId(result.getAwardId())
                    .awardIndex(result.getSort())
                    .build());
        }catch (Exception e){
            e.printStackTrace();
            return Response.error();
        }
    }

    @PostMapping("/strategy_rule_weight")
    @Override
    public Response<List<StrategyRuleWeightResponseDTO>> getStrategyRuleWeight(@RequestBody StrategyRuleWeightRequestDTO request) {
        log.info("[request]-[查询策略权重抽奖规则]");
        try {
            //参数校验
            Long activityId = request.getActivityId();
            String userId = request.getUserId();
            if(activityId == null || StringUtils.isBlank(userId)){
                return Response.error(Constants.Exception.invalid_params);
            }
            //查询用户累计抽奖次数
            Integer userRaffleCount = raffleActivityQuota.getUserRaffleCount(activityId, userId);
            //查询策略权重规则详细信息
            List<RuleWeightVO> ruleWeightVOS = raffleRule.getStrategyRuleWeightDetailByActivityId(activityId);
            if(ruleWeightVOS == null){
                return Response.success();
            }
            ArrayList<StrategyRuleWeightResponseDTO> responseDTOS = new ArrayList<>(ruleWeightVOS.size());
            for (RuleWeightVO ruleWeightVO : ruleWeightVOS) {
                List<RuleWeightVO.Award> awardList = ruleWeightVO.getAwardList();
                ArrayList<StrategyRuleWeightResponseDTO.StrategyAward> strategyAwards = new ArrayList<>(awardList.size());
                for (RuleWeightVO.Award award : awardList) {
                    strategyAwards.add(StrategyRuleWeightResponseDTO.StrategyAward.builder()
                            .awardId(award.getAwardId())
                            .awardTitle(award.getAwardTitle())
                            .build());
                }
                responseDTOS.add(StrategyRuleWeightResponseDTO.builder()
                        .userActivityAccountTotalUseCount(userRaffleCount)
                        .ruleWeightCount(ruleWeightVO.getWeight())
                        .strategyAwards(strategyAwards)
                        .build());
            }
            log.info("[request]-[查询策略权重抽奖规则] 成功");
            return Response.success(responseDTOS);
        } catch (AppException e){
            log.info("[request]-[查询策略权重抽奖规则] 失败:{}", e.getCode());
        } catch (Exception e){
            log.info("[request]-[查询策略权重抽奖规则] 失败:未知错误");
        }
        return Response.error();
    }

    /*log.info("[request]-[]");
    try {
        log.info("[request]-[] 成功");
        return Response.success();
    } catch (
    AppException e){
        log.info("[request]-[] 失败:{}", e.getCode());
    } catch (Exception e){
        log.info("[request]-[] 失败:未知错误");
    }
    return Response.error();*/
}
