package cn.evlight.api.model;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 策略奖项列表响应结果
 * @Author: evlight
 * @Date: 2024/5/29
 */

@Builder
@Data
public class StrategyAwardListResponseDTO {
    private Integer awardId; //奖品ID
    private String awardTitle; //奖品标题
    private String awardSubtitle; //奖品副标题
    private Integer sort; //排序
}
