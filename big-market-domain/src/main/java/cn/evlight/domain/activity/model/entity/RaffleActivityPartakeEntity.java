package cn.evlight.domain.activity.model.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 参与抽奖活动实体对象
 * @create 2024-04-04 20:02
 */
@Data
@Builder
public class RaffleActivityPartakeEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活动ID
     */
    private Long activityId;

}
