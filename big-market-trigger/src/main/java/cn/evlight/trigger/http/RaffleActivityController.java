package cn.evlight.trigger.http;

import cn.evlight.api.IRaffleActivityService;
import cn.evlight.api.model.request.RaffleActivityRequestDTO;
import cn.evlight.api.model.response.RaffleActivityResponseDTO;
import cn.evlight.domain.activity.model.entity.RaffleActivityPartakeEntity;
import cn.evlight.domain.activity.model.entity.UserRaffleOrderEntity;
import cn.evlight.domain.activity.service.IRaffleActivityPartake;
import cn.evlight.domain.activity.service.quota.armory.IActivityArmory;
import cn.evlight.domain.award.model.entity.UserAwardRecordEntity;
import cn.evlight.domain.award.model.valobj.AwardStateVO;
import cn.evlight.domain.award.service.IAwardService;
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
    private IRaffleActivityPartake raffleActivityPartake;

    @Autowired
    private IAwardService awardService;

    @GetMapping("/armory")
    @Override
    public Response<Boolean> assembleRaffleActivityArmory(@RequestParam Long activityId) {
        log.info("活动抽奖装配");
        try {
            //装配策略
            managerStrategyArmory.assembleRaffleStrategyByActivityId(activityId);
            //装配活动
            activityArmory.assembleActivitySkuByActivityId(activityId);
            log.info("装配成功");
            return Response.success();
        } catch (AppException e) {
            log.error("装配失败:{}",e.getMessage());
        }catch (Exception e){
            log.error("装配失败:未知错误");
            e.printStackTrace();
        }
        return Response.error();
    }

    @PostMapping("/raffle")
    @Override
    public Response<RaffleActivityResponseDTO> activityRaffle(@RequestBody RaffleActivityRequestDTO requestDTO) {
        log.info("活动抽奖");
        try {
            //参数校验
            Long activityId = requestDTO.getActivityId();
            String userId = requestDTO.getUserId();
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
            log.info("活动抽奖成功");
            //返回结果
            return Response.success(RaffleActivityResponseDTO.builder()
                            .awardId(raffleResultEntity.getAwardId())
                            .awardTitle(raffleResultEntity.getAwardTitle())
                            .awardIndex(raffleResultEntity.getSort())
                    .build());
        } catch (AppException e) {
            log.error("活动抽奖失败:{}", e.getCode());
        } catch (Exception e){
            log.error("活动抽奖失败:未知错误");
            e.printStackTrace();
        }
        return Response.error();
    }
}
