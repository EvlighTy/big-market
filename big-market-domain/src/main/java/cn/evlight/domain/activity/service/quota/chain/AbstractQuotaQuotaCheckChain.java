package cn.evlight.domain.activity.service.quota.chain;

/**
 * @Description: 校验链抽象类
 * @Author: evlight
 * @Date: 2024/6/2
 */
public abstract class AbstractQuotaQuotaCheckChain implements IQuotaCheckChain {

    protected IQuotaCheckChain next;

    @Override
    public IQuotaCheckChain next() {
        return next;
    }

    @Override
    public IQuotaCheckChain add(IQuotaCheckChain next) {
        this.next = next;
        return next;
    }

}
