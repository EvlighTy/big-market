package cn.evlight.domain.activity.model.entity;

import cn.evlight.domain.activity.model.valobj.OrderTradeTypeVO;
import lombok.Builder;
import lombok.Data;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 活动商品充值实体对象
 * @create 2024-03-23 09:11
 */
@Data
@Builder
public class RaffleActivityQuotaEntity {

    /** 用户ID */
    private String userId;
    /** 商品SKU - activity + activity count */
    private Long sku;
    /** 幂等业务单号，外部谁充值谁透传，这样来保证幂等（多次调用也能确保结果唯一，不会多次充值）。 */
    private String outBizId;
    //交易类型
    private OrderTradeTypeVO orderTradeTypeVO;

}
