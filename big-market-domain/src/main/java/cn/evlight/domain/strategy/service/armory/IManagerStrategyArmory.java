package cn.evlight.domain.strategy.service.armory;

/**
 * @Description: 策略管理接口
 * @Author: evlight
 * @Date: 2024/5/25
 */
public interface IManagerStrategyArmory {

    /**
     * @Description: 生成策略随机表并缓存到redis
     * @Param: [strategyId] 策略ID
     * @return: boolean
     * @Date: 2024/5/24
     */
    boolean assembleRaffleStrategy(Long strategyId);

    /**
    * @Description: 根据活动ID装配策略库
    * @Param: [activityId]
    * @return:
    * @Date: 2024/6/7
    */
    boolean assembleRaffleStrategyByActivityId(Long activityId);

}
