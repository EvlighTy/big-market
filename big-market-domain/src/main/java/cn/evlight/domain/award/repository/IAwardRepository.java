package cn.evlight.domain.award.repository;

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
}
