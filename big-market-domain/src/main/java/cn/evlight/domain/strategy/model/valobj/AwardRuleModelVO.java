package cn.evlight.domain.strategy.model.valobj;

import cn.evlight.domain.strategy.service.rule.filter.factory.before.DefaultRuleFilterChainFactory;
import cn.evlight.types.common.Constants;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;

/**
 * @Description: 奖项规则模型值对象
 * @Author: evlight
 * @Date: 2024/5/26
 */

@Getter
@Builder
public class AwardRuleModelVO {

    private String ruleModels;

    public String[] getDuringRuleModels(){
        return Arrays.stream(ruleModels.split(Constants.Split.COMMA))
                .filter(DefaultRuleFilterChainFactory.RuleModel::isDuringRuleModel)
                .toArray(String[]::new);
    }

    public String[] getAfterRuleModels(){
        return Arrays.stream(ruleModels.split(Constants.Split.COMMA))
                .filter(DefaultRuleFilterChainFactory.RuleModel::isAfterRuleModel)
                .toArray(String[]::new);
    }

}
