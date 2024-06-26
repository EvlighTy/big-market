package cn.evlight.domain.activity.model.aggregate;

import cn.evlight.domain.activity.model.entity.RaffleActivityAccountDayEntity;
import cn.evlight.domain.activity.model.entity.RaffleActivityAccountEntity;
import cn.evlight.domain.activity.model.entity.RaffleActivityAccountMonthEntity;
import cn.evlight.domain.activity.model.entity.UserRaffleOrderEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 参与活动订单聚合对象
 * @create 2024-04-05 08:31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePartakeOrderAggregate {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 账户总额度
     */
    private RaffleActivityAccountEntity raffleActivityAccount;

    /**
     * 是否存在月账户
     */
    private boolean isExistAccountMonth = true;

    /**
     * 账户月额度
     */
    private RaffleActivityAccountMonthEntity raffleActivityAccountMonthEntity;

    /**
     * 是否存在日账户
     */
    private boolean isExistAccountDay = true;

    /**
     * 账户日额度
     */
    private RaffleActivityAccountDayEntity raffleActivityAccountDayEntity;

    /**
     * 抽奖单实体
     */
    private UserRaffleOrderEntity userRaffleOrderEntity;

}
