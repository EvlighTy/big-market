package cn.evlight.domain.credit.service;

import cn.evlight.domain.credit.model.entity.CreditEntity;

/**
 * @Description: 积分服务接口
 * @Author: evlight
 * @Date: 2024/6/11
 */
public interface ICreditService {

    String createOrder(CreditEntity creditEntity);

}
