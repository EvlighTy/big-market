package cn.evlight.domain.strategy.repository;

import cn.evlight.domain.strategy.model.entity.StrategyAwardEntity;
import cn.evlight.domain.strategy.model.entity.StrategyEntity;
import cn.evlight.domain.strategy.model.entity.StrategyRuleEntity;

import java.util.LinkedHashMap;
import java.util.List;

public interface IStrategyRepository {

    /**
    * @Description: 根据策略ID查询奖项规则列表
    * @Author: evlight
    * @Date: 2024/5/24
    */
    List<StrategyAwardEntity> getList(Long strategyId);

    /**
    * @Description: 缓存概率范围，策略查找map到redis
    * @Author: evlight
    * @Date: 2024/5/25
    */
    void saveStrategyRandomMap2Redis(Long strategyId, int rateRange, LinkedHashMap<Integer, Integer> strategyRandomMap);

    /**
    * @Description: 获取概率范围
    * @Author: evlight
    * @Date: 2024/5/25
    */
    Integer getRateRange(String key);

    /**
    * @Description: 随机获取奖项
    * @Author: evlight
    * @Date: 2024/5/25
    */
    Integer getRandomAwardId(String key, int randomIndex);

    /**
     * @Description: 缓存每个奖项概率范围到redis
     * @Author: evlight
     * @Date: 2024/5/25
     */
    void saveAwardRateList2Redis(String key, int rateRange, LinkedHashMap<Integer, Integer> rateMap);


    /**
    * @Description: 从策略表中获取策略规则
    * @Param: [strategyId] 策略ID
    * @return: 策略规则实体
    * @Date: 2024/5/25
    */
    StrategyEntity getStrategyEntity(Long strategyId);

    /** 
    * @Description: 获取策略规则值
    * @Param: [strategyId, ruleModel] 策略ID， 规则类型
    * @return: 策略规则实体
    * @Date: 2024/5/25
    */
    StrategyRuleEntity getStrategyRuleValue(Long strategyId, String ruleModel);

    String getStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel);
}
