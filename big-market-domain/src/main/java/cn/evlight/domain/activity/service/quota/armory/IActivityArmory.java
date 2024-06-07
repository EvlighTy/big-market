package cn.evlight.domain.activity.service.quota.armory;

/**
 * @Description: 活动预热装配接口
 * @Author: evlight
 * @Date: 2024/6/4
 */
public interface IActivityArmory {

    /**
    * @Description: 预热活动sku信息
    * @Param: [sku]
    * @return:
    * @Date: 2024/6/7
    */
    boolean assembleActivitySkuBySku(Long sku);


    /**
    * @Description: 预热活动sku信息
    * @Param: [activityId]
    * @return:
    * @Date: 2024/6/7
    */
    boolean assembleActivitySkuByActivityId(Long activityId);

}
