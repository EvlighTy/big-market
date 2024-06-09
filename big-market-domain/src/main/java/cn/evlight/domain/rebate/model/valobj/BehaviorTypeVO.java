package cn.evlight.domain.rebate.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 行为类型值对象
 * @Author: evlight
 * @Date: 2024/6/9
 */

@Getter
@AllArgsConstructor
public enum BehaviorTypeVO {
    SIGN("sign", "签到（日历）"),
    OPENAI_PAY("openai_pay", "openai 外部支付完成"),
            ;

    private final String code;
    private final String info;
}
