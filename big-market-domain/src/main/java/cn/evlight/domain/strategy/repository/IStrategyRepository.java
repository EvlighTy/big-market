package cn.evlight.domain.strategy.repository;

import cn.evlight.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import cn.evlight.domain.strategy.model.entity.StrategyAwardEntity;
import cn.evlight.domain.strategy.model.entity.StrategyEntity;
import cn.evlight.domain.strategy.model.entity.StrategyRuleEntity;
import cn.evlight.domain.strategy.model.valobj.AwardRuleModelVO;
import cn.evlight.domain.strategy.model.valobj.RuleTreeVO;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface IStrategyRepository {

    /**
     * table: strategy_award
    * @Description: 查询策略奖品列表
    * @Param: [strategyId] 策略ID
    * @return:
    * @Date: 2024/5/24
    */
    List<StrategyAwardEntity> getStrategyAwardList(Long strategyId);

    /**
    * @Description: 获取策略奖品概率范围
    * @Param: [key]
    * @return:
    * @Date: 2024/5/29
    */
    Integer getRateRange(String key);

    /**
    * @Description: 随机获取奖品
    * @Param: [key, randomIndex]
    * @return:
    * @Date: 2024/5/29
    */
    Integer getRandomAwardId(String key, int randomIndex);

    /**
    * @Description: 缓存随机抽奖所需数据
    * @Param: [key, rateRange, rateMap]
    * @return:
    * @Date: 2024/5/29
    */
    void saveAwardRateList2Redis(String key, int rateRange, LinkedHashMap<Integer, Integer> rateMap);

    /**
     * table: strategy
    * @Description: 查询策略实体
    * @Param: [strategyId] 策略ID
    * @return: 策略规则实体
    * @Date: 2024/5/25
    */
    StrategyEntity getStrategyEntity(Long strategyId);

    /**
     * table: strategy_rule
    * @Description: 查询策略规则实体
    * @Param: [strategyId, ruleModel] 策略ID，规则类型
    * @return: 策略规则实体
    * @Date: 2024/5/25
    */
    StrategyRuleEntity getStrategyRuleEntity(Long strategyId, String ruleModel);

    /**
    * @Description: 查询策略或奖项规则具体值
    * @Param: [strategyId, awardId, ruleModel] 策略ID，奖项ID，规则类型
    * @return:
    * @Date: 2024/5/26
    */
    String getStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel);

    /**
     * table: strategy_award
    * @Description: 查询策略奖品规则模型
    * @Param: [strategyId, awardId] 策略ID，奖项ID
    * @return:
    * @Date: 2024/5/26
    */
    AwardRuleModelVO getStrategyAwardRuleModels(Long strategyId, Integer awardId);

    /**
     * table: rule_tree, rule_tree_node, rule_tree_node_line
    * @Description: 获取规则树
    * @Param: [treeId] 树ID
    * @return: 树实体
    * @Date: 2024/5/28
    */
    RuleTreeVO getRuleTree(String treeId);

    /**
     * table: strategy_award
    * @Description: 查询策略奖品实体
    * @Param: [strategyId, awardId]
    * @return:
    * @Date: 2024/5/30
    */
    StrategyAwardEntity getStrategyAwardEntity(Long strategyId, Integer awardId);

    /**
    * @Description: 获取奖品库存更新延迟队列
    * @Param: []
    * @return:
    * @Date: 2024/6/4
    */
    StrategyAwardStockKeyVO takeQueueValue();

    /**
     * tableL: strategy_award
    * @Description: 更新奖品库存
    * @Param: [strategyId, awardId]
    * @return:
    * @Date: 2024/6/4
    */
    void updateStrategyAwardStock(Long strategyId, Integer awardId);

    /**
     * table: raffle_activity
    * @Description: 根据活动ID查询策略ID
    * @Param: [activityId]
    * @return:
    * @Date: 2024/6/7
    */
    Long getStrategyIdByActivityId(Long activityId);

    /**
     * table: raffle_activity_account_day
    * @Description: 查询用户今天抽奖次数
    * @Param: []
    * @return:
    * @Date: 2024/6/7
    */
    Integer getUserRaffleCountToday(String userId, Long strategyId);

    /**
     * table: rule_tree_node
    * @Description: 查询策略奖品解锁阈值
    * @Param: [treeIds]
    * @return:
    * @Date: 2024/6/8
    */
    Map<String, Integer> getAwardRuleLockCount(String[] treeIds);

    /**
    * @Description: 扣减策略奖品库存
    * @Param: [cacheKey, endDateTime]
    * @return:
    * @Date: 2024/6/8
    */
    boolean subtractStrategyAwardStock(String cacheKey, LocalDateTime endDateTime);

    /**
    * @Description: 写入数据库库存更新延迟队列
    * @Param: [strategyAwardStockKeyVO]
    * @return:
    * @Date: 2024/6/8
    */
    void sendToStrategyAwardConsumeQueue(StrategyAwardStockKeyVO strategyAwardStockKeyVO);

    /**
    * @Description: 缓存策略奖品库存
    * @Param: [strategyId, awardId, awardCountSurplus]
    * @return:
    * @Date: 2024/6/8
    */
    void cacheStrategyAwardStock(String cacheKey, Integer awardCountSurplus);
}
