package cn.evlight.domain.activity.service.partake.chain;

/**
 * @Description: 校验接口链装配类
 * @Author: evlight
 * @Date: 2024/6/2
 */
public interface IPartakeCheckChainArmory {

    IPartakeCheckChain next();

    IPartakeCheckChain add(IPartakeCheckChain next);

}
