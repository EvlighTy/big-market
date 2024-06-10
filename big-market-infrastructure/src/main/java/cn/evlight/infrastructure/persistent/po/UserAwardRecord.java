package cn.evlight.infrastructure.persistent.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户中奖记录表
 * </p>
 *
 * @author evlight
 * @since 2024-06-06
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_award_record_000")
public class UserAwardRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 抽奖策略ID
     */
    private Long strategyId;

    /**
     * 抽奖订单ID【作为幂等使用】
     */
    private String orderId;

    /**
     * 奖品ID
     */
    private Integer awardId;

    /**
     * 奖品标题（名称）
     */
    private String awardTitle;

    /**
     * 中奖时间
     */
    private LocalDateTime awardTime;

    /**
     * 奖品状态；create-创建、completed-发奖完成
     */
    private String awardState;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
