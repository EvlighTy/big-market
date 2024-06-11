package cn.evlight.domain.award.service.distribute.impl;

import cn.evlight.domain.award.model.aggregate.DistributeAwardsAggregate;
import cn.evlight.domain.award.model.entity.CreditAwardEntity;
import cn.evlight.domain.award.model.entity.DistributeAwardEntity;
import cn.evlight.domain.award.model.entity.UserAwardRecordEntity;
import cn.evlight.domain.award.model.valobj.AwardStateVO;
import cn.evlight.domain.award.repository.IAwardRepository;
import cn.evlight.domain.award.service.distribute.IDistributeAward;
import cn.evlight.types.common.Constants;
import cn.evlight.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * @Description: 随机积分奖品
 * @Author: evlight
 * @Date: 2024/6/11
 */
@Slf4j
@Component("random_credit")
public class RandomCreditAward implements IDistributeAward {

    @Autowired
    private IAwardRepository awardRepository;

    @Override
    public void distributeAward(DistributeAwardEntity distributeAwardEntity) {
        log.info("[发放奖品] 随机积分");
        String awardConfig = distributeAwardEntity.getAwardConfig();
        if(awardConfig == null){
            //奖品配置没有透传
            awardConfig = awardRepository.getAwardConfig(distributeAwardEntity.getAwardId());
        }
        String[] creditRange = awardConfig.split(",");
        if(creditRange.length != 2){
            throw new AppException(Constants.ExceptionInfo.AWARD_CONFIG_ERROR_CONFIGURED);
        }
        BigDecimal randomCredit = generateRandomCredit(new BigDecimal(creditRange[0]), new BigDecimal(creditRange[1]));
        CreditAwardEntity creditAwardEntity = CreditAwardEntity.builder()
                .userId(distributeAwardEntity.getUserId())
                .creditAmount(randomCredit)
                .build();
        UserAwardRecordEntity userAwardRecordEntity = UserAwardRecordEntity.builder()
                .userId(creditAwardEntity.getUserId())
                .awardId(distributeAwardEntity.getAwardId())
                .orderId(distributeAwardEntity.getOrderId())
                .awardState(AwardStateVO.complete)
                .build();
        DistributeAwardsAggregate distributeAwardsAggregate = DistributeAwardsAggregate.builder()
                .userId(userAwardRecordEntity.getUserId())
                .userAwardRecordEntity(userAwardRecordEntity)
                .creditAwardEntity(creditAwardEntity)
                .build();
        awardRepository.saveDistributeAwardsAggregate(distributeAwardsAggregate);
    }

    private BigDecimal generateRandomCredit(BigDecimal min, BigDecimal max) {
        if(min.equals(max)){
            //最小值 = 最大值
            return min;
        }
        BigDecimal randomCredit = min.add(BigDecimal.valueOf(Math.random()).multiply(max.subtract(min)));
        return randomCredit.round(new MathContext(3));
    }


}
