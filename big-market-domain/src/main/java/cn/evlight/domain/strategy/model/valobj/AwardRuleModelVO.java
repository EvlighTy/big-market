package cn.evlight.domain.strategy.model.valobj;

import cn.evlight.domain.strategy.service.rule.filter.factory.after.DefaultRuleFilterTreeFactory;
import lombok.Builder;
import lombok.Getter;

/**
 * @Description: 奖项规则模型值对象
 * @Author: evlight
 * @Date: 2024/5/26
 */

@Getter
@Builder
public class AwardRuleModelVO {

    private String ruleModels;

    public String getTreeModel(){
        try {
            return DefaultRuleFilterTreeFactory.TreeModel.valueOf(ruleModels.toUpperCase()).getCode();
        }catch (Exception e){
            return null;
        }
    }
}
