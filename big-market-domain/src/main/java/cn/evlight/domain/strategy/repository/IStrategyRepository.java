package cn.evlight.domain.strategy.repository;

import cn.evlight.domain.strategy.model.entity.StrategyAwardEntity;
import cn.evlight.domain.strategy.model.entity.StrategyEntity;
import cn.evlight.domain.strategy.model.entity.StrategyRuleEntity;
import cn.evlight.domain.strategy.model.valobj.AwardRuleModelVO;

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
    * @Description: 查询策略实体
    * @Param: [strategyId] 策略ID
    * @return: 策略规则实体
    * @Date: 2024/5/25
    */
    StrategyEntity getStrategyEntity(Long strategyId);

    /** 
    * @Description: 查询策略规则实体
    * @Param: [strategyId, ruleModel] 策略ID，规则类型
    * @return: 策略规则实体
    * @Date: 2024/5/25
    */
    StrategyRuleEntity getStrategyRuleValue(Long strategyId, String ruleModel);

    /**
    * @Description: 查询策略或奖项规则值
    * @Param: [strategyId, awardId, ruleModel] 策略ID，奖项ID，规则类型
    * @return:
    * @Date: 2024/5/26
    */
    String getStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel);

    /**
    * @Description: 查询奖项规则
    * @Param: [strategyId, awardId] 策略ID，奖项ID
    * @return:
    * @Date: 2024/5/26
    */
    AwardRuleModelVO getAwardRuleModels(Long strategyId, Integer awardId);
}
