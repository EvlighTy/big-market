package cn.evlight.domain.activity.service;

import cn.evlight.domain.activity.model.valobj.ActivitySkuStockKeyVO;

/**
 * @Description: SKU库存操作接口
 * @Author: evlight
 * @Date: 2024/6/4
 */
public interface IRaffleActivitySkuStock {

    /**
    * @Description: 获取SKU库存延迟更新队列
    * @Param: []
    * @return:
    * @Date: 2024/6/4
    */
    ActivitySkuStockKeyVO takeQueueValue() throws InterruptedException;

    /**
    * @Description: 清空队列
    * @Param: []
    * @return:
    * @Date: 2024/6/4
    */
    void clearQueueValue(Long sku);

    /**
    * @Description: 更新数据库sku库存
    * @Param: [sku]
    * @return:
    * @Date: 2024/6/4
    */
    void updateActivitySkuStock(Long sku);

    /**
    * @Description: 清空数据库sku库存
    * @Param: [sku]
    * @return:
    * @Date: 2024/6/4
    */
    void clearActivitySkuStock(Long sku);

    /**
    * @Description: 判断库存是否已清空
    * @Param: [cacheKey]
    * @return:
    * @Date: 2024/6/8
    */
    boolean SkuStockIsZero(String cacheKey);
}
