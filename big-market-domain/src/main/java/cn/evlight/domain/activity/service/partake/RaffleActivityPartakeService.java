package cn.evlight.domain.activity.service.partake;

import cn.evlight.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import cn.evlight.domain.activity.model.entity.*;
import cn.evlight.domain.activity.model.valobj.UserRaffleOrderStateVO;
import cn.evlight.domain.activity.repository.IActivityRepository;
import cn.evlight.domain.activity.service.partake.chain.factory.DefaultPartakeCheckChainFactory;
import cn.evlight.types.common.Constants;
import cn.evlight.types.exception.AppException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Description: 抽奖活动参与实现类
 * @Author: evlight
 * @Date: 2024/6/6
 */

@Service
public class RaffleActivityPartakeService extends AbstractRaffleActivityPartake{

    private final DateTimeFormatter monthDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
    private final DateTimeFormatter dayDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public RaffleActivityPartakeService(DefaultPartakeCheckChainFactory defaultPartakeCheckChainFactory, IActivityRepository activityRepository) {
        super(defaultPartakeCheckChainFactory, activityRepository);
    }

    @Override
    protected UserRaffleOrderEntity buildUserRaffleOrder(Long activityId, String userId, LocalDateTime currentTime, ActivityEntity activityEntity) {
        return UserRaffleOrderEntity.builder()
                .activityId(activityId)
                .userId(userId)
                .activityName(activityEntity.getActivityName())
                .strategyId(activityEntity.getStrategyId())
                .orderId(RandomStringUtils.randomNumeric(12))
                .orderTime(currentTime)
                .orderState(UserRaffleOrderStateVO.create)
                .build();
    }

    @Override
    protected CreatePartakeOrderAggregate checkAccount(Long activityId, String userId, LocalDateTime currentTime) {
        //查询总额度
        RaffleActivityAccountEntity raffleActivityAccountEntity = activityRepository.queryRaffleActivityAccount(activityId, userId);
        if (raffleActivityAccountEntity == null || raffleActivityAccountEntity.getTotalCount() <= 0){
            //总额度账户不存在 或 总额度不足
            throw new AppException(Constants.ExceptionInfo.USER_QUOTA_INSUFFICIENT);
        }
        //查询月额度
        String monthDateTime = monthDateTimeFormatter.format(currentTime);
        RaffleActivityAccountMonthEntity raffleActivityAccountMonthEntity = activityRepository.queryRaffleActivityAccountMonth(activityId, userId, monthDateTime);
        if(raffleActivityAccountMonthEntity != null && raffleActivityAccountMonthEntity.getMonthCountSurplus() <= 0){
            //月额度账户存在 且 月额度不足
            throw new AppException(Constants.ExceptionInfo.USER_QUOTA_INSUFFICIENT);
        }
        boolean isExistAccountMonth = raffleActivityAccountMonthEntity != null;
        if(raffleActivityAccountMonthEntity == null){
            //创建月额度账户
            raffleActivityAccountMonthEntity = RaffleActivityAccountMonthEntity.builder()
                    .activityId(activityId)
                    .userId(userId)
                    .month(monthDateTime)
                    .monthCount(raffleActivityAccountEntity.getMonthCount())
                    .monthCountSurplus(raffleActivityAccountEntity.getMonthCountSurplus())
                    .build();
        }
        //查询日额度
        String dayDateTime = dayDateTimeFormatter.format(currentTime);
        RaffleActivityAccountDayEntity raffleActivityAccountDayEntity = activityRepository.queryRaffleActivityAccountDay(activityId, userId, dayDateTime);
        if(raffleActivityAccountDayEntity != null && raffleActivityAccountDayEntity.getDayCountSurplus() <=0){
            //日额度账户存在 且 月额度不足
            throw new AppException(Constants.ExceptionInfo.USER_QUOTA_INSUFFICIENT);
        }
        boolean isExistAccountDay = raffleActivityAccountDayEntity != null;
        if(raffleActivityAccountDayEntity == null){
            //创建日额度账户
            raffleActivityAccountDayEntity = RaffleActivityAccountDayEntity.builder()
                        .userId(userId)
                        .activityId(activityId)
                        .day(dayDateTime)
                        .dayCount(raffleActivityAccountEntity.getDayCount())
                        .dayCountSurplus(raffleActivityAccountEntity.getDayCountSurplus())
                        .build();
        }
        //构建聚合对象
        return CreatePartakeOrderAggregate.builder()
                .activityId(activityId)
                .userId(userId)
                .raffleActivityAccount(raffleActivityAccountEntity)
                .isExistAccountMonth(isExistAccountMonth)
                .raffleActivityAccountMonthEntity(raffleActivityAccountMonthEntity)
                .isExistAccountDay(isExistAccountDay)
                .raffleActivityAccountDayEntity(raffleActivityAccountDayEntity)
                .build();
    }
}
