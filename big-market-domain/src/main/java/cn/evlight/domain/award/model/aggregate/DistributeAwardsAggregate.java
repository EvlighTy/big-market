package cn.evlight.domain.award.model.aggregate;

import cn.evlight.domain.award.model.entity.CreditAwardEntity;
import cn.evlight.domain.award.model.entity.UserAwardRecordEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 发放奖品聚合对象
 * @create 2024-05-18 09:26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DistributeAwardsAggregate {
    /** 用户ID */
    private String userId;
    /** 用户发奖记录 */
    private UserAwardRecordEntity userAwardRecordEntity;
    /** 用户积分奖品 */
    private CreditAwardEntity creditAwardEntity;
}
