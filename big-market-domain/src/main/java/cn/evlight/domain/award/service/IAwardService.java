package cn.evlight.domain.award.service;

import cn.evlight.domain.award.model.entity.UserAwardRecordEntity;

/**
 * @Description: 奖品发放接口
 * @Author: evlight
 * @Date: 2024/6/7
 */
public interface IAwardService {

    void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity);

}
