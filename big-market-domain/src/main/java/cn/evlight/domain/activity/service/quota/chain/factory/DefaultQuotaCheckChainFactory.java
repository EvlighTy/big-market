package cn.evlight.domain.activity.service.quota.chain.factory;

import cn.evlight.domain.activity.service.quota.chain.IQuotaCheckChain;
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
public class DefaultQuotaCheckChainFactory {

    private final Map<String, IQuotaCheckChain> checkChainMap;

    public DefaultQuotaCheckChainFactory(Map<String, IQuotaCheckChain> checkChainMap) {
        this.checkChainMap = checkChainMap;
    }

    public IQuotaCheckChain openCheckChain(){
        //头节点
        IQuotaCheckChain head = checkChainMap.get(CheckModels.base_check.getCode());
        IQuotaCheckChain current = head;
        current = current.add(checkChainMap.get(CheckModels.sku_check.getCode()));
        return head;
    }


    @Getter
    @AllArgsConstructor
    public enum CheckModels {

        base_check("quota_base_check", "活动基础校验(活动库存, 活动状态, 活动时间)"),
        sku_check("sku_check", "活动sku校验")
        ;

        private final String code;
        private final String info;
    }

}
