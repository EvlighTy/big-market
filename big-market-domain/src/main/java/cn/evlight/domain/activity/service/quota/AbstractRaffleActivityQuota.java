package cn.evlight.domain.activity.service.quota;

import cn.evlight.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import cn.evlight.domain.activity.model.entity.ActivityCountEntity;
import cn.evlight.domain.activity.model.entity.ActivityEntity;
import cn.evlight.domain.activity.model.entity.ActivitySkuEntity;
import cn.evlight.domain.activity.model.entity.RaffleActivityQuotaEntity;
import cn.evlight.domain.activity.repository.IActivityRepository;
import cn.evlight.domain.activity.service.IRaffleActivityQuota;
import cn.evlight.domain.activity.service.quota.chain.IQuotaCheckChain;
import cn.evlight.domain.activity.service.quota.chain.factory.DefaultQuotaCheckChainFactory;
import cn.evlight.domain.activity.service.quota.policy.ITradePolicy;
import cn.evlight.types.common.Constants;
import cn.evlight.types.exception.AppException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
* @Description: 抽奖活动抽象类
* @Author: evlight
* @Date: 2024/6/1
*/
@Slf4j
public abstract class AbstractRaffleActivityQuota extends RaffleActivitySupport implements IRaffleActivityQuota {

    protected final Map<String, ITradePolicy> tradePolicyMap;

    public AbstractRaffleActivityQuota(DefaultQuotaCheckChainFactory defaultCheckChainFactory, IActivityRepository activityRepository, Map<String, ITradePolicy> tradePolicyMap) {
        super(defaultCheckChainFactory, activityRepository);
        this.tradePolicyMap = tradePolicyMap;
    }


    @Override
    public String createQuotaOrder(RaffleActivityQuotaEntity raffleActivityQuotaEntity) {
        Long sku = raffleActivityQuotaEntity.getSku();
        String outBusinessNo = raffleActivityQuotaEntity.getOutBizId();
        String userId = raffleActivityQuotaEntity.getUserId();
        //参数校验
        if(sku == null || StringUtils.isBlank(outBusinessNo) || StringUtils.isBlank(userId)){
            throw new AppException(Constants.ExceptionInfo.INVALID_PARAMS);
        }
        //查询库存单位(SKU)实体
        ActivitySkuEntity activitySkuEntity = queryActivitySku(sku);
        //查询活动实体
        ActivityEntity activityEntity = queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());
        //查询活动次数实体
        ActivityCountEntity activityCountEntity = queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());
        log.info("查询结果：活动SKU:{}\n活动:{}\n活动次数:{}",
                JSON.toJSONString(activitySkuEntity),
                JSON.toJSONString(activityEntity),
                JSON.toJSONString(activityCountEntity));
        //活动信息校验
        IQuotaCheckChain checkChain = defaultCheckChainFactory.openCheckChain();
        checkChain.doCheck(activitySkuEntity, activityEntity, activityCountEntity);
        //构建活动订单聚合对象
        CreateQuotaOrderAggregate createQuotaOrderAggregate = buildQuotaOrderAggregate(raffleActivityQuotaEntity, activitySkuEntity, activityEntity, activityCountEntity);
        //保存订单
        ITradePolicy tradePolicy = tradePolicyMap.get(raffleActivityQuotaEntity.getOrderTradeTypeVO().getCode());
        if(tradePolicy == null){
            log.error("没有对应的交易策略实现类");
            return null;
        }
        tradePolicy.trade(createQuotaOrderAggregate);
        //返回订单ID
        return createQuotaOrderAggregate.getActivityOrderEntity().getOrderId();
    }

    /**
    * @Description: 保存订单
    * @Param: [createQuotaOrderAggregate] 订单聚合对象
    * @return:
    * @Date: 2024/6/2
    */
//    protected abstract void saveOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate);

    /**
    * @Description: 构建订单聚合对象
    * @Param: [raffleActivityQuotaEntity, activitySkuEntity, activityEntity, activityCountEntity]
    * @return:
    * @Date: 2024/6/2
    */
    protected abstract CreateQuotaOrderAggregate buildQuotaOrderAggregate(RaffleActivityQuotaEntity raffleActivityQuotaEntity, ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity);

}
