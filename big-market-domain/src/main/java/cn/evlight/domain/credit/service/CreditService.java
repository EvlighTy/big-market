package cn.evlight.domain.credit.service;

import cn.evlight.domain.credit.model.aggregate.CreditAggregate;
import cn.evlight.domain.credit.model.entity.CreditEntity;
import cn.evlight.domain.credit.model.entity.UserCreditAccountEntity;
import cn.evlight.domain.credit.model.entity.UserCreditOrderEntity;
import cn.evlight.domain.credit.repository.ICreditRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: 积分服务实现类
 * @Author: evlight
 * @Date: 2024/6/11
 */
@Service
public class CreditService implements ICreditService{

    @Autowired
    private ICreditRepository creditRepository;

    @Override
    public String createOrder(CreditEntity creditEntity) {
        //创建积分账户实体
        UserCreditAccountEntity userCreditAccountEntity = UserCreditAccountEntity.builder()
                .userId(creditEntity.getUserId())
                .adjustAmount(creditEntity.getAmount())
                .build();
        //创建积分订单实体
        UserCreditOrderEntity userCreditOrderEntity = UserCreditOrderEntity.builder()
                .userId(creditEntity.getUserId())
                .orderId(RandomStringUtils.randomNumeric(12))
                .tradeName(creditEntity.getTradeName())
                .tradeType(creditEntity.getTradeType())
                .tradeAmount(creditEntity.getAmount())
                .outBusinessNo(creditEntity.getOutBusinessNo())
                .build();
        //构建聚合对象
        CreditAggregate creditAggregate = CreditAggregate.builder()
                .userId(creditEntity.getUserId())
                .userCreditAccountEntity(userCreditAccountEntity)
                .userCreditOrderEntity(userCreditOrderEntity)
                .build();
        //保存聚合对象
        creditRepository.saveCreditAggregate(creditAggregate);
        return userCreditOrderEntity.getOrderId();
    }

}
