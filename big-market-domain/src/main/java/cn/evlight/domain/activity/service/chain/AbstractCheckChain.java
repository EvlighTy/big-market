package cn.evlight.domain.activity.service.chain;

/**
 * @Description: 校验链抽象类
 * @Author: evlight
 * @Date: 2024/6/2
 */
public abstract class AbstractCheckChain implements ICheckChain{

    protected ICheckChain next;

    @Override
    public ICheckChain next() {
        return next;
    }

    @Override
    public ICheckChain add(ICheckChain next) {
        this.next = next;
        return next;
    }

}
