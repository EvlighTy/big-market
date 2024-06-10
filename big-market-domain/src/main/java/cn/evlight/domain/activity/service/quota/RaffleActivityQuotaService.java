package cn.evlight.domain.activity.service.quota;

import cn.evlight.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import cn.evlight.domain.activity.model.entity.*;
import cn.evlight.domain.activity.model.valobj.ActivitySkuStockKeyVO;
import cn.evlight.domain.activity.model.valobj.OrderStateVO;
import cn.evlight.domain.activity.repository.IActivityRepository;
import cn.evlight.domain.activity.service.IRaffleActivitySkuStock;
import cn.evlight.domain.activity.service.quota.chain.factory.DefaultQuotaCheckChainFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Description: 抽奖活动服务
 * @Author: evlight
 * @Date: 2024/6/1
 */

@Service
public class RaffleActivityQuotaService extends AbstractRaffleActivityQuota implements IRaffleActivitySkuStock {

    public RaffleActivityQuotaService(IActivityRepository activityRepository, DefaultQuotaCheckChainFactory defaultCheckChainFactory) {
        super(activityRepository, defaultCheckChainFactory);
    }

    @Override
    protected void saveOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        activityRepository.saveOrder(createQuotaOrderAggregate);
    }

    @Override
    protected CreateQuotaOrderAggregate buildQuotaOrderAggregate(RaffleActivityQuotaEntity raffleActivityQuotaEntity, ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {
        // 订单实体对象
        ActivityOrderEntity activityOrderEntity = new ActivityOrderEntity();
        activityOrderEntity.setUserId(raffleActivityQuotaEntity.getUserId());
        activityOrderEntity.setSku(raffleActivityQuotaEntity.getSku());
        activityOrderEntity.setActivityId(activityEntity.getActivityId());
        activityOrderEntity.setActivityName(activityEntity.getActivityName());
        activityOrderEntity.setStrategyId(activityEntity.getStrategyId());
        activityOrderEntity.setOrderId(RandomStringUtils.randomNumeric(12)); //随机生成订单号
        activityOrderEntity.setOrderTime(new Date());
        activityOrderEntity.setTotalCount(activityCountEntity.getTotalCount());
        activityOrderEntity.setDayCount(activityCountEntity.getDayCount());
        activityOrderEntity.setMonthCount(activityCountEntity.getMonthCount());
        activityOrderEntity.setState(OrderStateVO.completed);
        activityOrderEntity.setOutBusinessNo(raffleActivityQuotaEntity.getOutBizId());

        // 构建聚合对象
        return CreateQuotaOrderAggregate.builder()
                .userId(raffleActivityQuotaEntity.getUserId())
                .activityId(activitySkuEntity.getActivityId())
                .activityOrderEntity(activityOrderEntity)
                .build();
    }


    @Override
    public ActivitySkuStockKeyVO takeQueueValue() throws InterruptedException {
        return activityRepository.takeQueueValue();
    }

    @Override
    public void clearQueueValue(Long sku) {
        activityRepository.clearQueueValue(sku);
    }

    @Override
    public void updateActivitySkuStock(Long sku) {
        activityRepository.updateActivitySkuStock(sku);
    }

    @Override
    public void clearActivitySkuStock(Long sku) {
        activityRepository.clearActivitySkuStock(sku);
    }

    @Override
    public boolean SkuStockIsZero(String cacheKey) {
        return activityRepository.SkuStockIsZero(cacheKey);
    }

    @Override
    public Integer getUserRaffleCountToday(Long activityId, String userId) {
        return activityRepository.getUserRaffleCountToday(activityId, userId);
    }

    @Override
    public RaffleActivityAccountEntity getUserAccountQuota(Long activityId, String userId) {
        return activityRepository.getUserAccountQuota(activityId, userId);
    }

    @Override
    public Integer getUserRaffleCount(Long activityId, String userId) {
        return activityRepository.getUserRaffleCount(activityId, userId);
    }

}
