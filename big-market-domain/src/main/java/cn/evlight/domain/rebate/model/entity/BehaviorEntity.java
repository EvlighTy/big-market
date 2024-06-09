package cn.evlight.domain.rebate.model.entity;

import cn.evlight.domain.rebate.model.valobj.BehaviorTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 行为实体
 * @Author: evlight
 * @Date: 2024/6/9
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BehaviorEntity {
    private String userId;
    private BehaviorTypeVO behaviorTypeVO;
    private String outBizId;
}
