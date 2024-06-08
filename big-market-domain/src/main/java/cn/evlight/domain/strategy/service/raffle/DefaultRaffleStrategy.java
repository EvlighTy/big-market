package cn.evlight.domain.strategy.service.raffle;

import cn.evlight.domain.strategy.model.entity.RuleFilterParamEntity;
import cn.evlight.domain.strategy.model.entity.StrategyAwardEntity;
import cn.evlight.domain.strategy.model.valobj.AwardRuleModelVO;
import cn.evlight.domain.strategy.model.valobj.RuleTreeVO;
import cn.evlight.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import cn.evlight.domain.strategy.repository.IStrategyRepository;
import cn.evlight.domain.strategy.service.AbstractRaffleStrategy;
import cn.evlight.domain.strategy.service.IRaffleAward;
import cn.evlight.domain.strategy.service.IRaffleRule;
import cn.evlight.domain.strategy.service.IRaffleStock;
import cn.evlight.domain.strategy.service.ruleFilter.AbstractBeforeRuleFilter;
import cn.evlight.domain.strategy.service.ruleFilter.factory.after.DefaultRuleFilterTreeFactory;
import cn.evlight.domain.strategy.service.ruleFilter.factory.after.engine.impl.RuleFilterTreeEngine;
import cn.evlight.domain.strategy.service.ruleFilter.factory.before.DefaultRuleFilterChainFactory;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Description: 默认抽奖实现类
 * @Author: evlight
 * @Date: 2024/5/27
 */

@Slf4j
@Service
public class DefaultRaffleStrategy extends AbstractRaffleStrategy implements IRaffleAward, IRaffleStock, IRaffleRule {

    @Autowired
    protected IStrategyRepository strategyRepository;

    @Autowired
    private DefaultRuleFilterChainFactory ruleFilterChainFactory;

    @Autowired
    private DefaultRuleFilterTreeFactory ruleFilterTreeFactory;

    @Override
    public DefaultRuleFilterChainFactory.ResultData doBeforeRaffle(RuleFilterParamEntity ruleFilterParamEntity) {
        AbstractBeforeRuleFilter ruleFilter = ruleFilterChainFactory.openRuleFilterChain(ruleFilterParamEntity.getStrategyId());
        return ruleFilter.doFilter(ruleFilterParamEntity);
    }

    @Override
    public DefaultRuleFilterTreeFactory.ResultData doAfterRaffle(RuleFilterParamEntity ruleFilterParamEntity) {
        AwardRuleModelVO awardRuleModelVO = strategyRepository.getStrategyAwardRuleModels(ruleFilterParamEntity.getStrategyId(), ruleFilterParamEntity.getAwardId());
        if(awardRuleModelVO == null){
            //奖项没有配置规则
            return DefaultRuleFilterTreeFactory.ResultData.builder()
                    .awardId(ruleFilterParamEntity.getAwardId())
                    .build();
        }
        log.info("奖项规则:{}", JSON.toJSONString(awardRuleModelVO));
        String treeId = awardRuleModelVO.getRuleModels();
        if(treeId == null){
            //规则树为空
            log.info("规则树为空");
            return DefaultRuleFilterTreeFactory.ResultData.builder()
                    .awardId(ruleFilterParamEntity.getAwardId())
                    .build();
        }
        log.info("规则树id:{}", treeId);
        RuleTreeVO ruleTreeVO = strategyRepository.getRuleTree(treeId);
        RuleFilterTreeEngine ruleFilterTreeEngine = ruleFilterTreeFactory.openRuleFilterTree(ruleTreeVO);
        return ruleFilterTreeEngine.process(ruleFilterParamEntity);
    }

    @Override
    public List<StrategyAwardEntity> getStrategyAwardList(Long strategyId) {
        return strategyRepository.getStrategyAwardList(strategyId);
    }

    @Override
    public List<StrategyAwardEntity> getStrategyAwardListByActivityId(Long activityId) {
        Long strategyId = strategyRepository.getStrategyIdByActivityId(activityId);
        return getStrategyAwardList(strategyId);
    }

    @Override
    public StrategyAwardStockKeyVO takeQueueValue() throws InterruptedException {
        return strategyRepository.takeQueueValue();
    }

    @Override
    public void updateStrategyAwardStock(Long strategyId, Integer awardId) {
        strategyRepository.updateStrategyAwardStock(strategyId, awardId);
    }

    @Override
    public Map<String, Integer> getAwardRuleLockCount(String... treeIds) {
        return strategyRepository.getAwardRuleLockCount(treeIds);
    }
}
