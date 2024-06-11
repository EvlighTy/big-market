package cn.evlight.domain.award.repository;

import cn.evlight.domain.award.model.aggregate.DistributeAwardsAggregate;
import cn.evlight.domain.award.model.aggregate.UserAwardRecordAggregate;

/**
 * @Description: 奖品仓库接口
 * @Author: evlight
 * @Date: 2024/6/7
 */
public interface IAwardRepository {

    /**
     * table: user_award_record, task
    * @Description: 保存用户中奖记录 并 记录mq发送任务
    * @Param: [userAwardRecordAggregate]
    * @return:
    * @Date: 2024/6/7
    */
    void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);

    /**
     * table: user_award_record, user_credit_account
    * @Description: 发放奖品
    * @Param: [distributeAwardsAggregate]
    * @return:
    * @Date: 2024/6/11
    */
    void saveDistributeAwardsAggregate(DistributeAwardsAggregate distributeAwardsAggregate);

    /**
    * @Description: 查询奖品配置
    * @Param: [awardId]
    * @return:
    * @Date: 2024/6/11
    */
    String getAwardConfig(Integer awardId);

    /**
    * @Description: 获取奖品key
    * @Param: [awardId]
    * @return:
    * @Date: 2024/6/11
    */
    String getAwardKey(Integer awardId);
}
