package cn.evlight.domain.award.service.distribute;

import cn.evlight.domain.award.model.entity.DistributeAwardEntity;

/**
 * @Description: 奖品发放接口
 * @Author: evlight
 * @Date: 2024/6/11
 */
public interface IDistributeAward {

    void distributeAward(DistributeAwardEntity distributeAwardEntity);

}
