package cn.evlight.domain.activity.service.partake.chain.factory;

import cn.evlight.domain.activity.service.partake.chain.IPartakeCheckChain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description: 默认额度校验链工厂
 * @Author: evlight
 * @Date: 2024/6/6
 */

@Component
public class DefaultPartakeCheckChainFactory {

    private final Map<String, IPartakeCheckChain> checkChainMap;

    public DefaultPartakeCheckChainFactory(Map<String, IPartakeCheckChain> checkChainMap) {
        this.checkChainMap = checkChainMap;
    }

    public IPartakeCheckChain openCheckChain(){
        //头节点
        return checkChainMap.get(CheckModels.base_check.getCode());
    }


    @Getter
    @AllArgsConstructor
    public enum CheckModels {

        base_check("partake_base_check", "活动基础校验(活动状态, 活动时间)")
        ;

        private final String code;
        private final String info;
    }

}
