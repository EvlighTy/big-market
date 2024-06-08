package cn.evlight.api.model.response;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 策略奖项列表响应结果
 * @Author: evlight
 * @Date: 2024/5/29
 */

@Builder
@Data
public class RaffleStrategyAwardListResponseDTO {
    private Integer awardId; //奖品ID
    private String awardTitle; //奖品标题
    private String awardSubtitle; //奖品副标题
    private Integer sort; //排序
    /*private Integer ruleLockCount; //解锁阈值
    private Boolean isUnlocked; //是否解锁
    private Integer toUnlockCount; //还需要抽奖几次才能解锁*/
    private Integer awardRuleLockCount;
    // 奖品是否解锁 - true 已解锁、false 未解锁
    private Boolean isAwardUnlock;
    // 等待解锁次数 - 规定的抽奖N次解锁减去用户已经抽奖次数
    private Integer waitUnLockCount;
}
