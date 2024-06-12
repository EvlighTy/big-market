package cn.evlight.domain.credit.model.aggregate;

import cn.evlight.domain.credit.model.entity.TaskEntity;
import cn.evlight.domain.credit.model.entity.UserCreditAccountEntity;
import cn.evlight.domain.credit.model.entity.UserCreditOrderEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 交易聚合对象
 * @create 2024-06-01 09:48
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditAggregate {
    // 用户ID
    private String userId;
    // 积分账户实体
    private UserCreditAccountEntity userCreditAccountEntity;
    // 积分订单实体
    private UserCreditOrderEntity userCreditOrderEntity;
    private TaskEntity taskEntity;
}
