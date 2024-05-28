package cn.evlight.domain.strategy.service.rule.filter.factory.after;

import cn.evlight.domain.strategy.model.valobj.RuleFilterStateVO;
import cn.evlight.domain.strategy.model.valobj.RuleTreeVO;
import cn.evlight.domain.strategy.service.rule.filter.AbstractAfterRuleFilter;
import cn.evlight.domain.strategy.service.rule.filter.factory.after.engine.impl.RuleFilterTreeEngine;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Description: 默认过滤器规则树工厂
 * @Author: evlight
 * @Date: 2024/5/27
 */

@Service
public class DefaultRuleFilterTreeFactory {


    @Autowired
    private Map<String, AbstractAfterRuleFilter> ruleFilterMap;

    public RuleFilterTreeEngine openRuleFilterTree(RuleTreeVO ruleTreeVO){
        return new RuleFilterTreeEngine(ruleFilterMap, ruleTreeVO);
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Result {
        private RuleFilterStateVO state;
        private ResultData data;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResultData {
        /** 抽奖奖品ID - 内部流转使用 */
        private Integer awardId;
        /** 抽奖奖品规则 */
        private String awardRuleValue;
    }

    @Getter
    @AllArgsConstructor
    public enum TreeModel {

        TREE_LOCK("tree_lock", "抽奖次数达到阈值解锁奖品"),
        ;

        private final String code;
        private final String info;

    }

}
