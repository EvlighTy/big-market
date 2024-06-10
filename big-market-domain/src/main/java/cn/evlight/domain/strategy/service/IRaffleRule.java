package cn.evlight.domain.strategy.service;

import cn.evlight.domain.strategy.model.valobj.RuleWeightVO;

import java.util.List;
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

    /**
     * @Description: 查询策略权重规则详细信息
     * @Param: [strategyId]
     * @return:
     * @Date: 2024/6/10
     */
    List<RuleWeightVO> getStrategyRuleWeightDetail(Long strategyId);

    /**
    * @Description: 查询策略权重规则详细信息
    * @Param: [activityId]
    * @return:
    * @Date: 2024/6/10
    */
    List<RuleWeightVO> getStrategyRuleWeightDetailByActivityId(Long activityId);
}
