package cn.evlight.domain.activity.service;

import cn.evlight.domain.activity.model.aggregate.CreateOrderAggregate;
import cn.evlight.domain.activity.model.entity.*;
import cn.evlight.domain.activity.repository.IActivityRepository;
import cn.evlight.domain.activity.service.chain.ICheckChain;
import cn.evlight.domain.activity.service.chain.factory.DefaultCheckChainFactory;
import cn.evlight.types.common.Constants;
import cn.evlight.types.exception.AppException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
* @Description: 抽奖活动抽象类
* @Author: evlight
* @Date: 2024/6/1
*/
@Slf4j
public abstract class AbstractRaffleActivity extends RaffleActivitySupport implements IRaffleActivity {


    public AbstractRaffleActivity(IActivityRepository activityRepository, DefaultCheckChainFactory defaultCheckChainFactory) {
        super(activityRepository, defaultCheckChainFactory);
    }

    @Override
    public String createSkuRechargeOrder(SkuRechargeEntity skuRechargeEntity) {
        Long sku = skuRechargeEntity.getSku();
        String outBusinessNo = skuRechargeEntity.getOutBusinessNo();
        String userId = skuRechargeEntity.getUserId();
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
        ICheckChain checkChain = defaultCheckChainFactory.openCheckChain();
        boolean result = checkChain.doCheck(activitySkuEntity, activityEntity, activityCountEntity);
        //构建活动订单聚合对象
        CreateOrderAggregate createOrderAggregate = buildOrderAggregate(skuRechargeEntity, activitySkuEntity, activityEntity, activityCountEntity);
        //保存订单
        saveOrder(createOrderAggregate);
        //返回订单ID
        return createOrderAggregate.getActivityOrderEntity().getOrderId();
    }

    /**
    * @Description: 保存订单
    * @Param: [createOrderAggregate] 订单聚合对象
    * @return:
    * @Date: 2024/6/2
    */
    protected abstract void saveOrder(CreateOrderAggregate createOrderAggregate);

    /**
    * @Description: 构建订单聚合对象
    * @Param: [skuRechargeEntity, activitySkuEntity, activityEntity, activityCountEntity]
    * @return:
    * @Date: 2024/6/2
    */
    protected abstract CreateOrderAggregate buildOrderAggregate(SkuRechargeEntity skuRechargeEntity, ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity);

}
