package cn.evlight.domain.award.service;

import cn.evlight.domain.award.model.entity.DistributeAwardEntity;
import cn.evlight.domain.award.model.entity.UserAwardRecordEntity;

/**
 * @Description: 奖品服务接口
 * @Author: evlight
 * @Date: 2024/6/7
 */
public interface IAwardService {

    /**
    * @Description: 保存中奖记录
    * @Param: [userAwardRecordEntity]
    * @return:
    * @Date: 2024/6/11
    */
    void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity);

    /**
    * @Description: 发放奖品
    * @Param: [distributeAwardEntity]
    * @return:
    * @Date: 2024/6/11
    */
    void distributeAward(DistributeAwardEntity distributeAwardEntity);

}
