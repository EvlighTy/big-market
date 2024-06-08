package cn.evlight.domain.activity.service.quota.armory;

import java.time.LocalDateTime;

/**
 * @Description: 活动调度接口
 * @Author: evlight
 * @Date: 2024/6/4
 */
public interface IActivityDispatch {

    boolean subtractActivitySkuStockCount(Long sku, LocalDateTime endDate);

}
