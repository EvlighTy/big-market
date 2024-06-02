package cn.evlight.domain.activity.service.chain.factory;

import cn.evlight.domain.activity.service.chain.ICheckChain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description: 默认校验链工厂
 * @Author: evlight
 * @Date: 2024/6/2
 */

@Component
public class DefaultCheckChainFactory {

    private final Map<String, ICheckChain> checkChainMap;

    public DefaultCheckChainFactory(Map<String, ICheckChain> checkChainMap) {
        this.checkChainMap = checkChainMap;
    }

    public ICheckChain openCheckChain(){
        //头节点
        ICheckChain head = checkChainMap.get(CheckModels.base_check.getCode());
        ICheckChain current = head;
        current = current.add(checkChainMap.get(CheckModels.sku_check.code));
        return head;
    }


    @Getter
    @AllArgsConstructor
    public enum CheckModels {

        base_check("base_check", "活动基础校验(活动库存, 活动时间)"),
        sku_check("sku_check", "活动sku校验")
        ;

        private final String code;
        private final String info;
    }

}
