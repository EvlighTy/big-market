package cn.evlight.test.domain.strategy;

import cn.evlight.domain.strategy.model.entity.RaffleParamEntity;
import cn.evlight.domain.strategy.model.entity.RaffleResultEntity;
import cn.evlight.domain.strategy.model.entity.RuleFilterParamEntity;
import cn.evlight.domain.strategy.model.valobj.*;
import cn.evlight.domain.strategy.service.IRaffleStrategy;
import cn.evlight.domain.strategy.service.armory.IManagerStrategyArmory;
import cn.evlight.domain.strategy.service.armory.IUserStrategyArmory;
import cn.evlight.domain.strategy.service.ruleFilter.factory.after.DefaultRuleFilterTreeFactory;
import cn.evlight.domain.strategy.service.ruleFilter.factory.after.engine.impl.RuleFilterTreeEngine;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Description: 策略测试类
 * @Author: evlight
 * @Date: 2024/5/25
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyTest {

    @Autowired
    private IUserStrategyArmory userStrategyArmory;

    @Autowired
    private IManagerStrategyArmory managerStrategyArmory;

    @Autowired
    private IRaffleStrategy raffleStrategy;

    @Autowired
    private DefaultRuleFilterTreeFactory ruleFilterTreeFactory;

    @Test
    public void test1(){
        managerStrategyArmory.generateStrategyRandomMap(100006L);
    }

    @Test
    public void test2(){
        for (int i = 0; i < 100; i++) {
            log.info(userStrategyArmory.getRandomAwardId(100001L + ":4000").toString());
        }
    }

    @Test
    public void test3(){
        RaffleParamEntity raffleParamEntity = RaffleParamEntity.builder()
                .strategyId(100001L)
                .userId(16L)
                .build();
        RaffleResultEntity raffleResultEntity = raffleStrategy.doRaffle(raffleParamEntity);
        System.err.println(JSON.toJSONString(raffleResultEntity));
    }

    @Test
    public void test4(){
        //rule_lock
        RuleTreeNodeVO rule_lock = RuleTreeNodeVO.builder()
                .treeId("100000001")
                .ruleKey("rule_lock")
                .ruleDesc("限定用户已完成N次抽奖后解锁")
                .ruleValue("1")
                .treeNodeLineVOList(new ArrayList<RuleTreeNodeLineVO>() {{
                    add(RuleTreeNodeLineVO.builder()
                            .treeId("100000001")
                            .ruleNodeFrom("rule_lock")
                            .ruleNodeTo("rule_luck_award")
                            .ruleLimitType(RuleLimitTypeVO.EQUAL)
                            .ruleLimitValue(RuleFilterStateVO.TAKE_OVER)
                            .build());

                    add(RuleTreeNodeLineVO.builder()
                            .treeId("100000001")
                            .ruleNodeFrom("rule_lock")
                            .ruleNodeTo("rule_stock")
                            .ruleLimitType(RuleLimitTypeVO.EQUAL)
                            .ruleLimitValue(RuleFilterStateVO.ALLOW)
                            .build());
                }})
                .build();

        //rule_stock
        RuleTreeNodeVO rule_stock = RuleTreeNodeVO.builder()
                .treeId("100000001")
                .ruleKey("rule_stock")
                .ruleDesc("库存处理规则")
                .ruleValue(null)
                .treeNodeLineVOList(new ArrayList<RuleTreeNodeLineVO>() {{
                    add(RuleTreeNodeLineVO.builder()
                            .treeId("100000001")
                            .ruleNodeFrom("rule_stock")
                            .ruleNodeTo("rule_luck_award")
                            .ruleLimitType(RuleLimitTypeVO.EQUAL)
                            .ruleLimitValue(RuleFilterStateVO.TAKE_OVER)
                            .build());
                }})
                .build();

        //rule_luck_award
        RuleTreeNodeVO rule_luck_award = RuleTreeNodeVO.builder()
                .treeId("100000001")
                .ruleKey("rule_luck_award")
                .ruleDesc("限定用户已完成N次抽奖后解锁")
                .ruleValue("1")
                .treeNodeLineVOList(null)
                .build();


        RuleTreeVO ruleTreeVO = new RuleTreeVO();
        ruleTreeVO.setTreeId("100000001");
        ruleTreeVO.setTreeName("决策树规则；增加dall-e-3画图模型");
        ruleTreeVO.setTreeDesc("决策树规则；增加dall-e-3画图模型");
        ruleTreeVO.setTreeRootRuleNode("rule_lock");

        ruleTreeVO.setTreeNodeMap(new HashMap<String, RuleTreeNodeVO>() {{
            put("rule_lock", rule_lock);
            put("rule_stock", rule_stock);
            put("rule_luck_award", rule_luck_award);
        }});

        RuleFilterTreeEngine ruleFilterTreeEngine = ruleFilterTreeFactory.openRuleFilterTree(ruleTreeVO);
        RuleFilterParamEntity ruleFilterParamEntity = RuleFilterParamEntity.builder()
                .strategyId(100001L)
                .userId(16L)
                .awardId(109)
                .build();
        DefaultRuleFilterTreeFactory.ResultData data = ruleFilterTreeEngine.process(ruleFilterParamEntity);
        System.out.println(data);
    }

    @Test
    public void test5(){
        RuleTreeVO tree = userStrategyArmory.getRuleTree("tree_lock");
        System.out.println(JSON.toJSONString(tree));
    }

    @Test
    public void test6(){
        RaffleParamEntity raffleParamEntity = RaffleParamEntity.builder()
                .strategyId(100006L)
                .userId(16L)
                .build();
        RaffleResultEntity result = raffleStrategy.doRaffle(raffleParamEntity);
        System.out.println(result);
    }

}
