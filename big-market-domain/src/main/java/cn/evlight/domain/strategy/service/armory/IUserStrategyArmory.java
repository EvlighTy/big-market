package cn.evlight.domain.strategy.service.armory;

import cn.evlight.domain.strategy.model.valobj.RuleTreeVO;

import java.time.LocalDateTime;

/**
* @Description: 策略使用接口
* @Author: evlight
* @Date: 2024/5/24
*/

public interface IUserStrategyArmory {

    /**
    * @Description: 随机获取奖项ID
    * @Param: [strategyId] 策略ID
    * @return: 奖项ID
    * @Date: 2024/5/25
    */
    Integer getRandomAwardId(Long strategyId);

    /**
     * @Description: 权重规则随机获取奖项ID
     * @Param: [strategyId] 策略ID
     * @return: 奖项ID
     * @Date: 2024/5/25
     */
    Integer getRandomAwardId(String key);

    /**
    * @Description: 获取规则树
    * @Param: [treeId] 树ID
    * @return:
    * @Date: 2024/5/28
    */
    RuleTreeVO getRuleTree(String treeId);

    /**
    * @Description: 扣减策略奖品库存
    * @Param: [strategyId, awardId, endDateTime]
    * @return:
    * @Date: 2024/6/8
    */
    boolean subtractStrategyAwardStock(Long strategyId, Integer awardId, LocalDateTime endDateTime);
}
