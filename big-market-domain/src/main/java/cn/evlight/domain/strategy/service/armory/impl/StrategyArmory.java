package cn.evlight.domain.strategy.service.armory.impl;

import cn.evlight.domain.strategy.model.entity.StrategyAwardEntity;
import cn.evlight.domain.strategy.model.entity.StrategyEntity;
import cn.evlight.domain.strategy.model.entity.StrategyRuleEntity;
import cn.evlight.domain.strategy.model.valobj.RuleTreeVO;
import cn.evlight.domain.strategy.repository.IStrategyRepository;
import cn.evlight.domain.strategy.service.armory.IManagerStrategyArmory;
import cn.evlight.domain.strategy.service.armory.IUserStrategyArmory;
import cn.evlight.types.common.Constants;
import cn.evlight.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class StrategyArmory implements IUserStrategyArmory, IManagerStrategyArmory {

    @Autowired
    private IStrategyRepository strategyRepository;

    @Override
    public boolean assembleRaffleStrategy(Long strategyId) {
        //配置策略总奖项
        log.info("配置策略总奖项:{}", strategyId);
        List<StrategyAwardEntity> strategyAwardEntities = strategyRepository.getStrategyAwardList(strategyId);
        doGenerateStrategyRandomMap(strategyId.toString(), strategyAwardEntities);
        //配置策略权重奖项
        StrategyEntity strategyEntity = strategyRepository.getStrategyEntity(strategyId);
        if(strategyEntity == null){
            //策略无规则
            return true;
        }
        String ruleWeight = strategyEntity.getRuleWeight();
        if(ruleWeight == null) {
            //策略无权重规则
            return true;
        }
        StrategyRuleEntity strategyRuleEntity = strategyRepository.getStrategyRuleEntity(strategyId, ruleWeight);
        if(strategyRuleEntity == null) {
            //权重规则没有配置值
            throw new AppException(strategyId + ":权重规则没有配置值");
        }
        log.info("配置策略权重奖项:{}", strategyId);
        Map<String, Set<String>> ruleWeightValues = strategyRuleEntity.getRuleWeightValues();
        for (String key : ruleWeightValues.keySet()) {
            Set<String> ruleWeightValue = ruleWeightValues.get(key);
            ArrayList<StrategyAwardEntity> strategyAwardEntitiesClone = new ArrayList<>(strategyAwardEntities);
            strategyAwardEntitiesClone.removeIf(strategyAwardEntity -> !ruleWeightValue.contains(strategyAwardEntity.getAwardId().toString()));
            doGenerateStrategyRandomMap(strategyId + ":" + key, strategyAwardEntitiesClone);
        }
        return true;
    }

    @Override
    public boolean assembleRaffleStrategyByActivityId(Long activityId) {
        Long strategyId = strategyRepository.getStrategyIdByActivityId(activityId);
        assembleRaffleStrategy(strategyId);
        return true;
    }

    /**
    * @Description: 传入奖项集合生成随机配置
    * @Param: [strategyId, strategyAwardEntities] 策略ID，奖项集合
    * @return:
    * @Date: 2024/5/25
    */
    private void doGenerateStrategyRandomMap(String key, List<StrategyAwardEntity> strategyAwardEntities) {
        //最小概率
        BigDecimal minRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        log.info("最小概率:{}", minRate);
        //概率总和
        BigDecimal totalRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("概率总和:{}", totalRate);
        //概率范围
        BigDecimal rateRange = totalRate.divide(minRate, 10, RoundingMode.CEILING);
        log.info("概率范围:{}", rateRange);
        rateRange = smoothRateRange(rateRange);
        log.info("平滑后概率范围:{}", rateRange);
        log.info("==================================");

        //每项概率
        LinkedHashMap<Integer, Integer> rateMap = new LinkedHashMap<>(strategyAwardEntities.size());
        int sum = 0;
        Iterator<StrategyAwardEntity> iterator = strategyAwardEntities.iterator();
        while (iterator.hasNext()){
            StrategyAwardEntity strategyAwardEntity = iterator.next();
            Integer awardId = strategyAwardEntity.getAwardId();
            if(!iterator.hasNext()){
                //最后一个
                rateMap.put(awardId, rateRange.intValue() - sum);
            }
            int rate = strategyAwardEntity.getAwardRate()
                    .multiply(rateRange)
                    .setScale(10, RoundingMode.CEILING)
                    .divide(totalRate, 0, RoundingMode.CEILING)
                    .intValue();
            rateMap.put(awardId, rate);
            sum += rate;
        }
        //缓存概率范围，奖项概率数组到redis
        strategyRepository.saveAwardRateList2Redis(key, rateRange.intValue(), rateMap);
    }

    /**
    * @Description: 平滑概率范围
    * @Param: [rateRange]
    * @return:
    * @Date: 2024/5/29
    */
    private BigDecimal smoothRateRange(BigDecimal rateRange) {
        if(rateRange.stripTrailingZeros().scale() == 0){
            return rateRange;
        }
        BigDecimal result = rateRange.multiply(BigDecimal.valueOf(2));
        if(result.stripTrailingZeros().scale() == 0){
            return result;
        }
        while (rateRange.stripTrailingZeros().scale() > 0){
            rateRange = rateRange.multiply(BigDecimal.valueOf(10));
        }
        return rateRange;
    }

    @Override
    public Integer getRandomAwardId(Long strategyId) {
        return getRandomAwardId(strategyId.toString());
    }

    @Override
    public Integer getRandomAwardId(String key) {
        Integer rateRange = strategyRepository.getRateRange(key);
        return strategyRepository.getRandomAwardId(key, new SecureRandom().nextInt(rateRange));
    }

    @Override
    public RuleTreeVO getRuleTree(String treeId) {
        return strategyRepository.getRuleTree(treeId);
    }

    @Override
    public boolean subtractStrategyAwardStock(Long strategyId, Integer awardId, LocalDateTime endDateTime) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_STOCK_KEY + strategyId + Constants.Split.COLON + awardId;
        return strategyRepository.subtractStrategyAwardStock(cacheKey, endDateTime);
    }

}
