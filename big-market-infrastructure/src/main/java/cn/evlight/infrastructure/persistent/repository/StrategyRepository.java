package cn.evlight.infrastructure.persistent.repository;

import cn.evlight.domain.strategy.model.entity.StrategyAwardEntity;
import cn.evlight.domain.strategy.model.entity.StrategyEntity;
import cn.evlight.domain.strategy.model.entity.StrategyRuleEntity;
import cn.evlight.domain.strategy.model.valobj.*;
import cn.evlight.domain.strategy.repository.IStrategyRepository;
import cn.evlight.infrastructure.persistent.dao.*;
import cn.evlight.infrastructure.persistent.po.*;
import cn.evlight.infrastructure.persistent.redis.IRedisService;
import cn.evlight.types.common.Constants;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class StrategyRepository extends ServiceImpl<StrategyMapper, Strategy> implements IStrategyRepository {

    @Autowired
    private StrategyAwardMapper strategyAwardMapper;

    @Autowired
    private StrategyMapper strategyMapper;

    @Autowired
    private StrategyRuleMapper strategyRuleMapper;

    @Autowired
    private RuleTreeMapper ruleTreeMapper;

    @Autowired
    private RuleTreeNodeMapper ruleTreeNodeMapper;

    @Autowired
    private RuleTreeNodeLineMapper ruleTreeNodeLineMapper;

    @Autowired
    private IRedisService redisService;


    @Override
    public List<StrategyAwardEntity> getList(Long strategyId) {
        //先查询redis
        String key = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId;
        List<StrategyAwardEntity> strategyAwardEntities = redisService.getValue(key);
        if(strategyAwardEntities != null && !strategyAwardEntities.isEmpty()) return strategyAwardEntities;
        //再查询数据库
        LambdaQueryWrapper<StrategyAward> queryWrapper = new LambdaQueryWrapper<StrategyAward>()
                /*.select(StrategyAward::getStrategyId)
                .select(StrategyAward::getAwardId)
                .select(StrategyAward::getAwardCount)
                .select(StrategyAward::getAwardCountSurplus)
                .select(StrategyAward::getAwardRate)*/
                .eq(StrategyAward::getStrategyId, strategyId);
        List<StrategyAward> strategyAwards = strategyAwardMapper.selectList(queryWrapper);
        System.err.println(strategyAwards);
        strategyAwardEntities = new ArrayList<>(strategyAwards.size());
        for (StrategyAward strategyAward : strategyAwards) {
            StrategyAwardEntity strategyAwardEntity = StrategyAwardEntity.builder()
                        .strategyId(strategyAward.getStrategyId())
                        .awardId(strategyAward.getAwardId())
                        .awardCount(strategyAward.getAwardCount())
                        .awardCountSurplus(strategyAward.getAwardCountSurplus())
                        .awardRate(strategyAward.getAwardRate())
                        .build();
            strategyAwardEntities.add(strategyAwardEntity);
        }
        //缓存到redis
        redisService.setValue(key, strategyAwardEntities);
        return strategyAwardEntities;
    }

    @Override
    public void saveStrategyRandomMap2Redis(Long strategyId, int rateRange, LinkedHashMap<Integer, Integer> strategyRandomMap) {
        /*redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId, rateRange);
        RMap<Integer, Integer> redisCacheMap = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_MAP_KEY);
        redisCacheMap.putAll(strategyRandomMap);*/
    }

    @Override
    public Integer getRateRange(String key) {
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key);
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
        String key = Constants.RedisKey.STRATEGY_RULE_KEY + strategyId;
        StrategyEntity strategyEntity = redisService.getValue(key);
        if(strategyEntity != null) return strategyEntity;
        //再查询数据库
        LambdaQueryWrapper<Strategy> queryWrapper = new LambdaQueryWrapper<Strategy>()
                .eq(Strategy::getStrategyId, strategyId);
        Strategy strategy = strategyMapper.selectOne(queryWrapper);
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
    public StrategyRuleEntity getStrategyRuleValue(Long strategyId, String ruleModel) {
        //先从redis中查询
        String key = Constants.RedisKey.STRATEGY_RULE_VALUE_KEY + strategyId + ":" + ruleModel;
        StrategyRuleEntity strategyRuleEntity = redisService.getValue(key);
        if(strategyRuleEntity != null) return strategyRuleEntity;
        //再查询数据区
        LambdaQueryWrapper<StrategyRule> queryWrapper = new LambdaQueryWrapper<StrategyRule>()
                .eq(StrategyRule::getStrategyId, strategyId)
                .eq(StrategyRule::getRuleModel, ruleModel);
        StrategyRule strategyRule = strategyRuleMapper.selectOne(queryWrapper);
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
        LambdaQueryWrapper<StrategyRule> queryWrapper = new LambdaQueryWrapper<StrategyRule>()
                .eq(StrategyRule::getStrategyId, strategyId)
                .eq(StrategyRule::getRuleModel, ruleModel)
                .eq(awardId!=null, StrategyRule::getAwardId, awardId);
        StrategyRule strategyRule = strategyRuleMapper.selectOne(queryWrapper);
        if (strategyRule == null) return null;
        return strategyRule.getRuleValue();
    }

    @Override
    public AwardRuleModelVO getStrategyAwardRuleModels(Long strategyId, Integer awardId) {
        LambdaQueryWrapper<StrategyAward> queryWrapper = new LambdaQueryWrapper<StrategyAward>()
                .eq(StrategyAward::getStrategyId, strategyId)
                .eq(StrategyAward::getAwardId, awardId);
        StrategyAward strategyAward = strategyAwardMapper.selectOne(queryWrapper);
        if(strategyAward == null) return null;
        return AwardRuleModelVO.builder()
                .ruleModels(strategyAward.getRuleModels())
                .build();
    }

    @Override
    public RuleTreeVO getRuleTree(String treeId) {
        //先查询redis
        String key = Constants.RedisKey.STRATEGY_RULE_TREE_KEY + treeId;
        RuleTreeVO tree = redisService.getValue(key);
        if(tree != null) return tree;
        //根节点
        LambdaQueryWrapper<RuleTree> queryWrapper1 = new LambdaQueryWrapper<RuleTree>()
                .eq(RuleTree::getTreeId, treeId);
        RuleTree ruleTree = ruleTreeMapper.selectOne(queryWrapper1);
        //节点
        LambdaQueryWrapper<RuleTreeNode> queryWrapper2 = new LambdaQueryWrapper<RuleTreeNode>()
                .eq(RuleTreeNode::getTreeId, treeId);
        List<RuleTreeNode> ruleTreeNodes = ruleTreeNodeMapper.selectList(queryWrapper2);
        //节点分支
        LambdaQueryWrapper<RuleTreeNodeLine> queryWrapper3 = new LambdaQueryWrapper<RuleTreeNodeLine>()
                .eq(RuleTreeNodeLine::getTreeId, treeId);
        List<RuleTreeNodeLine> ruleTreeNodeLines = ruleTreeNodeLineMapper.selectList(queryWrapper3);
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

}
