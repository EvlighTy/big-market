package cn.evlight.infrastructure.persistent.repository;

import cn.evlight.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import cn.evlight.domain.strategy.model.entity.StrategyAwardEntity;
import cn.evlight.domain.strategy.model.entity.StrategyEntity;
import cn.evlight.domain.strategy.model.entity.StrategyRuleEntity;
import cn.evlight.domain.strategy.model.valobj.*;
import cn.evlight.domain.strategy.repository.IStrategyRepository;
import cn.evlight.infrastructure.persistent.dao.*;
import cn.evlight.infrastructure.persistent.po.*;
import cn.evlight.infrastructure.persistent.redis.IRedisService;
import cn.evlight.types.common.Constants;
import cn.evlight.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class StrategyRepository implements IStrategyRepository {

    @Autowired
    private IStrategyAwardDao strategyAwardMapper;

    @Autowired
    private IStrategyDao strategyMapper;

    @Autowired
    private IStrategyRuleDao strategyRuleMapper;

    @Autowired
    private IRuleTreeDao ruleTreeMapper;

    @Autowired
    private IRuleTreeNodeDao ruleTreeNodeMapper;

    @Autowired
    private IRuleTreeNodeLineDao ruleTreeNodeLineMapper;

    @Autowired
    private IRedisService redisService;

    @Autowired
    private IRaffleActivityDao raffleActivityDao;

    @Autowired
    private RaffleActivityAccountDayMapper raffleActivityAccountDayMapper;

    @Override
    public List<StrategyAwardEntity> getStrategyAwardList(Long strategyId) {
        //先查询redis
        String key = Constants.RedisKey.STRATEGY_AWARD_LIST_KEY + strategyId;
        List<StrategyAwardEntity> strategyAwardEntities = redisService.getValue(key);
        if(strategyAwardEntities != null && !strategyAwardEntities.isEmpty()) return strategyAwardEntities;
        //再查询数据库
        List<StrategyAward> strategyAwards = strategyAwardMapper.queryStrategyAwardListByStrategyId(strategyId);
        strategyAwardEntities = new ArrayList<>(strategyAwards.size());
        for (StrategyAward strategyAward : strategyAwards) {
            StrategyAwardEntity strategyAwardEntity = StrategyAwardEntity.builder()
                        .strategyId(strategyAward.getStrategyId())
                        .awardTitle(strategyAward.getAwardTitle())
                        .awardSubtitle(strategyAward.getAwardSubtitle())
                        .awardId(strategyAward.getAwardId())
                        .awardCount(strategyAward.getAwardCount())
                        .awardCountSurplus(strategyAward.getAwardCountSurplus())
                        .awardRate(strategyAward.getAwardRate())
                        .ruleModels(strategyAward.getRuleModels())
                        .sort(strategyAward.getSort())
                        .build();
            strategyAwardEntities.add(strategyAwardEntity);
        }
        //缓存到redis
        redisService.setValue(key, strategyAwardEntities);
        return strategyAwardEntities;
    }

    @Override
    public Integer getRateRange(String key) {
        Integer rateRange = redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key);
        if(rateRange == null){
            throw new AppException("未装配策略库:" + key);
        }
        return rateRange;
    }

    @Override
    public Integer getRandomAwardId(String key, int randomIndex) {
        RMap<Integer, Integer> rateMap = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_MAP_KEY + key);
        //使用轮盘赌算法
        int sum = 0;
        for (Map.Entry<Integer, Integer> entry : rateMap.entrySet()) {
            sum += entry.getValue();
            if(randomIndex < sum){
                return entry.getKey();
            }
        }
        return -1;
    }

    @Override
    public void saveAwardRateList2Redis(String key, int rateRange, LinkedHashMap<Integer, Integer> rateMap) {
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key, rateRange);
        RMap<Object, Object> redisCacheMap = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_MAP_KEY + key);
        redisCacheMap.putAll(rateMap);
    }

    @Override
    public StrategyEntity getStrategyEntity(Long strategyId) {
        //先查询redis
        String key = Constants.RedisKey.STRATEGY_KEY + strategyId;
        StrategyEntity strategyEntity = redisService.getValue(key);
        if(strategyEntity != null) return strategyEntity;
        //再查询数据库
        Strategy strategy = strategyMapper.queryStrategyByStrategyId(strategyId);
        if(strategy == null) return null;
        strategyEntity = StrategyEntity.builder()
                .strategyId(strategy.getStrategyId())
                .strategyDesc(strategy.getStrategyDesc())
                .ruleModels(strategy.getRuleModels())
                .build();
        redisService.setValue(key, strategyEntity);
        return strategyEntity;
    }

    @Override
    public StrategyRuleEntity getStrategyRuleEntity(Long strategyId, String ruleModel) {
        //先从redis中查询
        String key = Constants.RedisKey.STRATEGY_RULE_VALUE_KEY + strategyId + ":" + ruleModel;
        StrategyRuleEntity strategyRuleEntity = redisService.getValue(key);
        if(strategyRuleEntity != null) return strategyRuleEntity;
        //再查询数据区
        StrategyRule strategyRule = strategyRuleMapper.queryStrategyRule(StrategyRule.builder()
                .strategyId(strategyId)
                .ruleModel(ruleModel)
                .build());
        strategyRuleEntity = StrategyRuleEntity.builder()
                .strategyId(strategyRule.getStrategyId())
                .awardId(strategyRule.getAwardId())
                .ruleType(strategyRule.getRuleType())
                .ruleModel(strategyRule.getRuleModel())
                .ruleValue(strategyRule.getRuleValue())
                .ruleDesc(strategyRule.getRuleDesc())
                .build();
        redisService.setValue(key, strategyRuleEntity);
        return strategyRuleEntity;
    }

    @Override
    public String getStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel) {
        //先从redis中查询
        String key = Constants.RedisKey.STRATEGY_RULE_VALUE_KEY + strategyId + ":" + ruleModel;
        StrategyRuleEntity strategyRuleEntity = redisService.getValue(key);
        if(strategyRuleEntity != null) return strategyRuleEntity.getRuleValue();
        //再查询数据区
        strategyRuleEntity = getStrategyRuleEntity(strategyId, ruleModel);
        return strategyRuleEntity.getRuleValue();
    }

    @Override
    public AwardRuleModelVO getStrategyAwardRuleModels(Long strategyId, Integer awardId) {
        String ruleModels = strategyAwardMapper.queryStrategyAwardRuleModels(StrategyAward.builder()
                .strategyId(strategyId)
                .awardId(awardId)
                .build());
        if(ruleModels == null) return null;
        return AwardRuleModelVO.builder()
                .ruleModels(ruleModels)
                .build();
    }

    @Override
    public RuleTreeVO getRuleTree(String treeId) {
        //先查询redis
        String key = Constants.RedisKey.STRATEGY_RULE_TREE_KEY + treeId;
        RuleTreeVO tree = redisService.getValue(key);
        if(tree != null) return tree;
        //根节点
        RuleTree ruleTree = ruleTreeMapper.queryRuleTreeByTreeId(treeId);
        //节点
        List<RuleTreeNode> ruleTreeNodes = ruleTreeNodeMapper.queryRuleTreeNodeListByTreeId(treeId);
        //节点分支
        List<RuleTreeNodeLine> ruleTreeNodeLines = ruleTreeNodeLineMapper.queryRuleTreeNodeLineListByTreeId(treeId);
        //组合
        HashMap<String, RuleTreeNodeVO> nodeMap = new HashMap<>();
        for (RuleTreeNode ruleTreeNode : ruleTreeNodes) {
            //遍历每个节点
            String ruleKey = ruleTreeNode.getRuleKey();
            RuleTreeNodeVO ruleTreeNodeVO = RuleTreeNodeVO.builder()
                        .treeId(ruleTreeNode.getTreeId())
                        .ruleKey(ruleTreeNode.getRuleKey())
                        .ruleDesc(ruleTreeNode.getRuleDesc())
                        .ruleValue(ruleTreeNode.getRuleValue())
                        .build();
            ArrayList<RuleTreeNodeLineVO> treeLines = new ArrayList<>();
            for (RuleTreeNodeLine ruleTreeNodeLine : ruleTreeNodeLines) {
                //遍历每个节点分支
                if(ruleTreeNodeLine.getRuleNodeFrom().equals(ruleKey)){
                    //是该节点的节点分支
                    RuleTreeNodeLineVO nodeLine = RuleTreeNodeLineVO.builder()
                                        .treeId(ruleTreeNodeLine.getTreeId())
                                        .ruleNodeFrom(ruleTreeNodeLine.getRuleNodeFrom())
                                        .ruleNodeTo(ruleTreeNodeLine.getRuleNodeTo())
                                        .ruleLimitType(RuleLimitTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitType()))
                                        .ruleLimitValue(RuleFilterStateVO.valueOf(ruleTreeNodeLine.getRuleLimitValue()))
                                        .build();
                    treeLines.add(nodeLine);
                }
                ruleTreeNodeVO.setTreeNodeLineVOList(treeLines);
            }
            nodeMap.put(ruleKey, ruleTreeNodeVO);
        }
        tree = RuleTreeVO.builder()
                .treeId(ruleTree.getTreeId())
                .treeName(ruleTree.getTreeName())
                .treeDesc(ruleTree.getTreeDesc())
                .treeRootRuleNode(ruleTree.getTreeNodeRuleKey())
                .treeNodeMap(nodeMap)
                .build();
        redisService.setValue(key, tree);
        return tree;
    }

    @Override
    public StrategyAwardEntity getStrategyAwardEntity(Long strategyId, Integer awardId) {
        //先查询redis
        String key = Constants.RedisKey.STRATEGY_ENTITY_MAP_KEY + strategyId;
        RMap<Integer, StrategyAwardEntity> strategyAwardEntityMap = redisService.getMap(key);
        StrategyAwardEntity strategyAwardEntity = strategyAwardEntityMap.get(awardId);
        if(strategyAwardEntity != null){
            return strategyAwardEntity;
        }
        //再查询数据库
        StrategyAward strategyAward = strategyAwardMapper.queryStrategyAward(StrategyAward.builder()
                        .strategyId(strategyId)
                        .awardId(awardId)
                .build());
        strategyAwardEntity = StrategyAwardEntity.builder()
                .strategyId(strategyAward.getStrategyId())
                .awardTitle(strategyAward.getAwardTitle())
                .awardSubtitle(strategyAward.getAwardSubtitle())
                .awardId(strategyAward.getAwardId())
                .awardCount(strategyAward.getAwardCount())
                .awardCountSurplus(strategyAward.getAwardCountSurplus())
                .awardRate(strategyAward.getAwardRate())
                .sort(strategyAward.getSort())
                .build();
        strategyAwardEntityMap.put(strategyAward.getAwardId(), strategyAwardEntity);
        return strategyAwardEntity;
    }

    @Override
    public StrategyAwardStockKeyVO takeQueueValue() {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_QUERY_KEY;
        RBlockingQueue<StrategyAwardStockKeyVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
        return blockingQueue.poll();
    }

    @Override
    public void updateStrategyAwardStock(Long strategyId, Integer awardId) {
        strategyAwardMapper.updateStrategyAwardStock(StrategyAward.builder()
                        .strategyId(strategyId)
                        .awardId(awardId)
                .build());
    }

    @Override
    public Long getStrategyIdByActivityId(Long activityId) {
        return raffleActivityDao.queryStrategyIdByActivityId(activityId);
    }

    @Override
    public Integer getUserRaffleCountToday(String userId, Long strategyId) {
                Long activityId = raffleActivityDao.queryActivityIdByStrategyId(strategyId);
        RaffleActivityAccountDay raffleActivityAccountDay = raffleActivityAccountDayMapper.queryRaffleActivityAccountDay(RaffleActivityAccountDay.builder()
                .activityId(activityId)
                .userId(userId)
                .day(RaffleActivityAccountDay.getCurrentDay())
                .build());
        if (raffleActivityAccountDay == null){
            return 0;
        }
        return raffleActivityAccountDay.getDayCount() - raffleActivityAccountDay.getDayCountSurplus();
    }

    @Override
    public Map<String, Integer> getAwardRuleLockCount(String[] treeIds) {
        if (treeIds == null || treeIds.length == 0){
            return Collections.emptyMap();
        }
        List<RuleTreeNode> ruleTreeNodes = ruleTreeNodeMapper.getRuleLockValues(treeIds);
        return ruleTreeNodes.stream()
                .collect(Collectors.toMap(RuleTreeNode::getTreeId,
                        ruleTreeNode -> Integer.parseInt(ruleTreeNode.getRuleValue())));
    }

    @Override
    public boolean subtractStrategyAwardStock(String cacheKey, LocalDateTime endDateTime) {
        long stock = redisService.decr(cacheKey);
        if (stock < 0) {
            //奖品库存不足
            log.info("策略奖品库存不足:{}", cacheKey);
            redisService.setAtomicLong(cacheKey, 0);
            return false;
        }
        String lockKey = cacheKey + Constants.Split.COLON + (stock + 1);
        Boolean lock;
        if (endDateTime != null) {
            long expireMillis = endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
            lock = redisService.setNx(lockKey, expireMillis, TimeUnit.MILLISECONDS);
        } else {
            lock = redisService.setNx(lockKey);
        }
        if (!lock) {
            log.info("策略奖品库存加锁失败:{}", lockKey);
        }
        return lock;
    }

    @Override
    public void sendToStrategyAwardConsumeQueue(StrategyAwardStockKeyVO strategyAwardStockKeyVO) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_QUERY_KEY;
        RBlockingQueue<StrategyAwardStockKeyVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
        RDelayedQueue<StrategyAwardStockKeyVO> delayedQueue = redisService.getDelayedQueue(blockingQueue);
        delayedQueue.offer(strategyAwardStockKeyVO, 3, TimeUnit.SECONDS);
    }

    @Override
    public void cacheStrategyAwardStock(String cacheKey, Integer awardCountSurplus) {
        if(redisService.isExists(cacheKey)){
            return;
        }
        redisService.setAtomicLong(cacheKey, awardCountSurplus);
    }

}
