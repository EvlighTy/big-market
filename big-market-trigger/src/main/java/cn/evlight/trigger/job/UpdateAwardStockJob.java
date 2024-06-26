package cn.evlight.trigger.job;

import cn.evlight.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import cn.evlight.domain.strategy.service.IRaffleStock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 更新奖品库存任务；为了不让更新库存的压力打到数据库中，这里采用了redis更新缓存库存，异步队列更新数据库，数据库表最终一致即可。
 * @create 2024-02-09 12:13
 */
@Slf4j
@Component
public class UpdateAwardStockJob {

    @Resource
    private IRaffleStock raffleStock;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec() {
        try {
            log.info("[Scheduled Task]-[更新奖品库存] 开始执行...");
            StrategyAwardStockKeyVO strategyAwardStockKeyVO = raffleStock.takeQueueValue();
            if (null == strategyAwardStockKeyVO){
                log.info("[Scheduled Task]-[更新奖品库存] 未查询到消息");
                return;
            }
            log.info("[Scheduled Task]-[更新奖品库存] 查询到消息 strategyId:{} awardId:{}", strategyAwardStockKeyVO.getStrategyId(), strategyAwardStockKeyVO.getAwardId());
            raffleStock.updateStrategyAwardStock(strategyAwardStockKeyVO.getStrategyId(), strategyAwardStockKeyVO.getAwardId());
        } catch (Exception e) {
            log.error("[Scheduled Task]-[更新奖品库存] 执行失败", e);
        }
    }

}
