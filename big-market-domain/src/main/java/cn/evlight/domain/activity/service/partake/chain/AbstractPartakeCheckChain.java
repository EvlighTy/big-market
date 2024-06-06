package cn.evlight.domain.activity.service.partake.chain;

/**
 * @Description: 校验链抽象类
 * @Author: evlight
 * @Date: 2024/6/2
 */
public abstract class AbstractPartakeCheckChain implements IPartakeCheckChain {

    protected IPartakeCheckChain next;

    @Override
    public IPartakeCheckChain next() {
        return next;
    }

    @Override
    public IPartakeCheckChain add(IPartakeCheckChain next) {
        this.next = next;
        return next;
    }

}
