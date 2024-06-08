package cn.evlight.trigger.job;

import cn.evlight.domain.activity.model.valobj.ActivitySkuStockKeyVO;
import cn.evlight.domain.activity.service.IRaffleActivitySkuStock;
import cn.evlight.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 更新活动sku库存任务
 * @create 2024-03-30 09:52
 */
@Slf4j
@Component
public class UpdateActivitySkuStockJob {

    @Resource
    private IRaffleActivitySkuStock skuStock;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec() {
        try {
            log.info("定时任务 [更新活动sku库存] 开始执行...");
            ActivitySkuStockKeyVO activitySkuStockKeyVO = skuStock.takeQueueValue();
            if (null == activitySkuStockKeyVO){
                log.info("定时任务 [更新活动sku库存] 未查询到更新消息");
                return;
            }
            log.info("定时任务 [更新活动sku库存] 查询到消息 sku:{} activityId:{}", activitySkuStockKeyVO.getSku(), activitySkuStockKeyVO.getActivityId());
            Long sku = activitySkuStockKeyVO.getSku();
            String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_ZERO_KEY + sku;
            if (skuStock.SkuStockIsZero(cacheKey)){
                log.info("定时任务 [更新活动sku库存] 库存已清空无需处理此消息");
                return;
            }
            //更新数据库库存
            skuStock.updateActivitySkuStock(sku);
        } catch (Exception e) {
            log.error("定时任务 [更新活动sku库存] 执行失败", e);
        }
    }

}
