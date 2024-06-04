package cn.evlight.domain.activity.service.armory;

import java.util.Date;

/**
 * @Description: 活动调度接口
 * @Author: evlight
 * @Date: 2024/6/4
 */
public interface IActivityDispatch {

    boolean subtractActivitySkuStockCount(Long sku, Date endDate);

}
