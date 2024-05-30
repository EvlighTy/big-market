package cn.evlight.trigger.http;

import cn.evlight.api.IRaffleService;
import cn.evlight.api.model.RaffleRequestDTO;
import cn.evlight.api.model.RaffleResponseDTO;
import cn.evlight.api.model.StrategyAwardListRequestDTO;
import cn.evlight.api.model.StrategyAwardListResponseDTO;
import cn.evlight.domain.strategy.model.entity.RaffleParamEntity;
import cn.evlight.domain.strategy.model.entity.RaffleResultEntity;
import cn.evlight.domain.strategy.model.entity.StrategyAwardEntity;
import cn.evlight.domain.strategy.service.IRaffleAward;
import cn.evlight.domain.strategy.service.IRaffleStrategy;
import cn.evlight.domain.strategy.service.armory.IManagerStrategyArmory;
import cn.evlight.domain.strategy.service.armory.IUserStrategyArmory;
import cn.evlight.types.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 抽奖服务
 * @Author: evlight
 * @Date: 2024/5/29
 */

@Slf4j
@RestController()
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/")
public class RaffleController implements IRaffleService {

    @Autowired
    private IUserStrategyArmory userStrategyArmory;

    @Autowired
    private IManagerStrategyArmory managerStrategyArmory;

    @Autowired
    private IRaffleStrategy raffleStrategy;

    @Autowired
    private IRaffleAward raffleAward;

    @GetMapping("strategy_armory")
    @Override
    public Response<Boolean> assembleRaffleStrategyArmory(@RequestParam Long strategyId) {
        log.info("装配抽奖策略库");
        try {
            boolean result = managerStrategyArmory.generateStrategyRandomMap(strategyId);
            return Response.success(result);
        }catch (Exception e){
            e.printStackTrace();
            return Response.error();
        }
    }

    @PostMapping("raffle_award_list")
    @Override
    public Response<List<StrategyAwardListResponseDTO>> getStrategyAwardList(@RequestBody StrategyAwardListRequestDTO requestDTO) {
        log.info("查询策略奖品列表");
        try {
            List<StrategyAwardEntity> strategyAwardList = raffleAward.getStrategyAwardList(requestDTO.getStrategyId());
            ArrayList<StrategyAwardListResponseDTO> strategyAwardListResponseDTOS = new ArrayList<>();
            for (StrategyAwardEntity strategyAwardEntity : strategyAwardList) {
                StrategyAwardListResponseDTO strategyAwardListResponseDTO = StrategyAwardListResponseDTO.builder()
                                .awardId(strategyAwardEntity.getAwardId())
                                .awardTitle(strategyAwardEntity.getAwardTitle())
                                .awardSubtitle(strategyAwardEntity.getAwardSubtitle())
                                .sort(strategyAwardEntity.getSort())
                                .build();
                strategyAwardListResponseDTOS.add(strategyAwardListResponseDTO);
            }
            return Response.success(strategyAwardListResponseDTOS);
        }catch (Exception e){
            e.printStackTrace();
            return Response.error();
        }
    }

    @PostMapping("raffle")
    @Override
    public Response<RaffleResponseDTO> raffle(@RequestBody RaffleRequestDTO requestDTO) {
        log.info("抽奖");
        try {
            RaffleResultEntity result = raffleStrategy.doRaffle(RaffleParamEntity.builder()
                    .strategyId(requestDTO.getStrategyId())
                    .userId(0x1L)
                    .build());
            return Response.success(RaffleResponseDTO.builder()
                    .awardId(result.getAwardId())
                    .awardIndex(result.getSort())
                    .build());
        }catch (Exception e){
            e.printStackTrace();
            return Response.error();
        }
    }

}
