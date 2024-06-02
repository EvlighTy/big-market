package cn.evlight.domain.activity.service.chain;

/**
 * @Description: 校验接口链装配类
 * @Author: evlight
 * @Date: 2024/6/2
 */
public interface ICheckChainArmory {

    ICheckChain next();

    ICheckChain add(ICheckChain next);

}
