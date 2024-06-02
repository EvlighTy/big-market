package cn.evlight.domain.strategy.service.ruleFilter.factory.after.engine.impl;

import cn.evlight.domain.strategy.model.entity.RuleFilterParamEntity;
import cn.evlight.domain.strategy.model.valobj.RuleTreeNodeLineVO;
import cn.evlight.domain.strategy.model.valobj.RuleTreeNodeVO;
import cn.evlight.domain.strategy.model.valobj.RuleTreeVO;
import cn.evlight.domain.strategy.service.ruleFilter.AbstractAfterRuleFilter;
import cn.evlight.domain.strategy.service.ruleFilter.factory.after.DefaultRuleFilterTreeFactory;
import cn.evlight.domain.strategy.service.ruleFilter.factory.after.engine.IRuleFilterTreeEngine;

import java.util.List;
import java.util.Map;

/**
 * @Description: 过滤器规则树引擎
 * @Author: evlight
 * @Date: 2024/5/27
 */
public class RuleFilterTreeEngine implements IRuleFilterTreeEngine {

    private final Map<String, AbstractAfterRuleFilter> ruleFilterMap;
    private final RuleTreeVO ruleTreeVO;

    public RuleFilterTreeEngine(Map<String, AbstractAfterRuleFilter> ruleFilterMap, RuleTreeVO ruleTreeVO) {
        this.ruleFilterMap = ruleFilterMap;
        this.ruleTreeVO = ruleTreeVO;
    }

    @Override
    public DefaultRuleFilterTreeFactory.ResultData process(RuleFilterParamEntity ruleFilterParamEntity) {
        DefaultRuleFilterTreeFactory.ResultData data = null;
        //根节点
        Map<String, RuleTreeNodeVO> treeNodeMap = ruleTreeVO.getTreeNodeMap();
        String rootNodeName = ruleTreeVO.getTreeRootRuleNode();
        RuleTreeNodeVO currentNode = treeNodeMap.get(rootNodeName);
        while (currentNode != null){
            //当前节点对应的过滤器
            String key = currentNode.getRuleKey();
            AbstractAfterRuleFilter filter = ruleFilterMap.get(key);
            //过滤器执行
            ruleFilterParamEntity.setRuleValue(currentNode.getRuleValue());
            DefaultRuleFilterTreeFactory.Result result = filter.doFilter(ruleFilterParamEntity);
            data = result.getData();
            //下一个节点
            String nextNodeName = getNextNode(result.getState().getCode(), currentNode.getTreeNodeLineVOList());
            currentNode = treeNodeMap.get(nextNodeName);
        }
        return data;
    }

    /**
    * @Description: 下一个节点
    * @Param: [state, nodeLines]
    * @return:
    * @Date: 2024/5/28
    */
    private String getNextNode(String state, List<RuleTreeNodeLineVO> nodeLines) {
        if(nodeLines == null || nodeLines.isEmpty()){
            //子节点为空
            return null;
        }
        for (RuleTreeNodeLineVO nodeLine : nodeLines) {
            if(matchState(state, nodeLine)){
                return nodeLine.getRuleNodeTo();
            }
        }
        return null;
    }

    private boolean matchState(String state, RuleTreeNodeLineVO nodeLine) {
        String lineState = nodeLine.getRuleLimitValue().getCode();
        switch (nodeLine.getRuleLimitType()){
            case EQUAL:
                return state.equals(lineState);
            case GT:
            case LT:
            case GE:
            case LE:
            default:
                return false;
        }
    }


}
