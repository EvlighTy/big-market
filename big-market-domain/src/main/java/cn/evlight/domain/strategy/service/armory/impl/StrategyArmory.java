package cn.evlight.domain.strategy.service.armory.impl;

import cn.evlight.domain.strategy.model.entity.StrategyAwardEntity;
import cn.evlight.domain.strategy.model.entity.StrategyEntity;
import cn.evlight.domain.strategy.model.entity.StrategyRuleEntity;
import cn.evlight.domain.strategy.repository.IStrategyRepository;
import cn.evlight.domain.strategy.service.armory.IManagerStrategyArmory;
import cn.evlight.domain.strategy.service.armory.IUserStrategyArmory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;

@Slf4j
@Service
public class StrategyArmory implements IUserStrategyArmory, IManagerStrategyArmory {

    @Autowired
    private IStrategyRepository strategyRepository;

    @Override
    public boolean generateStrategyRandomMap(Long strategyId) {
        //配置策略奖项
        List<StrategyAwardEntity> strategyAwardEntities = strategyRepository.getList(strategyId);
        doGenerateStrategyRandomMap(strategyId.toString(), strategyAwardEntities);
        //配置策略权重
        StrategyEntity strategyEntity = strategyRepository.getStrategyEntity(strategyId);
        if(strategyEntity == null) return true;
        String ruleWeight = strategyEntity.getRuleWeight();
        if(ruleWeight == null) return true;
        //配置策略权重值
        StrategyRuleEntity strategyRuleEntity = strategyRepository.getStrategyRuleValue(strategyId, ruleWeight);
        if(strategyRuleEntity == null) return true;
        Map<String, Set<String>> ruleWeightValues = strategyRuleEntity.getRuleWeightValues();
        for (String key : ruleWeightValues.keySet()) {
            Set<String> ruleWeightValue = ruleWeightValues.get(key);
            ArrayList<StrategyAwardEntity> strategyAwardEntitiesClone = new ArrayList<>(strategyAwardEntities);
            strategyAwardEntitiesClone.removeIf(strategyAwardEntity -> !ruleWeightValue.contains(strategyAwardEntity.getAwardId().toString()));
            doGenerateStrategyRandomMap(strategyId + ":" + key, strategyAwardEntitiesClone);
        }
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
        BigDecimal rateRange = totalRate.divide(minRate, 0, RoundingMode.CEILING);
        log.info("概率范围:{}", rateRange);
        log.info("==================================");

        /*使用轮盘赌算法*/
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
                    .divide(totalRate, 10, RoundingMode.CEILING)
                    .multiply(rateRange)
                    .setScale(0, RoundingMode.FLOOR)
                    .intValue();
//            log.info("每项概率:{}->{}->{}", awardId, strategyAwardEntity.getAwardRate(), rate);
            rateMap.put(awardId, rate);
            sum += rate;
        }
        System.err.println(rateMap);
        //缓存概率范围，奖项概率数组到redis
        strategyRepository.saveAwardRateList2Redis(key, rateRange.intValue(), rateMap);

        /*使用集合*/
        /*//根据占位数量生成策略查找list
        ArrayList<Integer> strategyArrayList = new ArrayList<>(rateRange.intValue());
        Iterator<StrategyAwardEntity> iterator = strategyAwardEntities.iterator();
        while (iterator.hasNext()){
            StrategyAwardEntity strategyAwardEntity = iterator.next();
            Integer awardId = strategyAwardEntity.getAwardId();
            if(iterator.hasNext()){
                //最后一个元素
                for (int i = strategyArrayList.size(); i < rateRange.intValue(); i++) {
                    strategyArrayList.add(awardId);
                }
            }
            //循环次数
            int roundTimes = rateRange.multiply(strategyAwardEntity.getAwardRate()).setScale(0, RoundingMode.CEILING).intValue();
            for (int i = 0; i < roundTimes; i++) {
                strategyArrayList.add(awardId);
            }
        }
        //打乱策略查找list
        Collections.shuffle(strategyArrayList);
        //生成策略查找map
        LinkedHashMap<Integer, Integer> strategyRandomMap = new LinkedHashMap<>(strategyArrayList.size());
        for (int i = 0; i < strategyArrayList.size(); i++) {
            strategyRandomMap.put(i,strategyArrayList.get(i));
        }
        //缓存概率范围，策略查找map到redis
        strategyRepository.saveStrategyRandomMap2Redis(strategyId,rateRange.intValue(),strategyRandomMap);*/
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

}
