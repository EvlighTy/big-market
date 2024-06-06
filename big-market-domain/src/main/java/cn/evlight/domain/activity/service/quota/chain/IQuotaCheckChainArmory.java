package cn.evlight.domain.activity.service.quota.chain;

/**
 * @Description: 校验接口链装配类
 * @Author: evlight
 * @Date: 2024/6/2
 */
public interface IQuotaCheckChainArmory {

    IQuotaCheckChain next();

    IQuotaCheckChain add(IQuotaCheckChain next);

}
