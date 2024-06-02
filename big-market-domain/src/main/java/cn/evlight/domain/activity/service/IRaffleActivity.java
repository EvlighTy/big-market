package cn.evlight.domain.activity.service;

import cn.evlight.domain.activity.model.entity.SkuRechargeEntity;

/**
 * @Description: 抽奖活动接口
 * @Author: evlight
 * @Date: 2024/6/1
 */
public interface IRaffleActivity {

    String createSkuRechargeOrder(SkuRechargeEntity skuRechargeEntity);

}
