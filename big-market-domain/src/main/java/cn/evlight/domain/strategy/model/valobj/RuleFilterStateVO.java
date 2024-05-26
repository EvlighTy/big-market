package cn.evlight.domain.strategy.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 规则过滤校验类型值对象
 * @create 2024-01-06 11:10
 */
@Getter
@AllArgsConstructor
public enum RuleFilterStateVO {

    ALLOW("0000", "放行"),
    TAKE_OVER("0001","接管"),
    ;

    private final String code;
    private final String info;

}
