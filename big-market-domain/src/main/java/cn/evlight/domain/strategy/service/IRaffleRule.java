package cn.evlight.domain.strategy.service;

import java.util.Map;

/**
 * @Description: 抽奖规则接口
 * @Author: evlight
 * @Date: 2024/6/8
 */
public interface IRaffleRule {

    /**
    * @Description: 获取策略奖品解锁阈值
    * @Param: [treeIds]
    * @return:
    * @Date: 2024/6/8
    */
    Map<String, Integer> getAwardRuleLockCount(String... treeIds);

}
