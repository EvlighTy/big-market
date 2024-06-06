package cn.evlight.domain.activity.service.partake;

import cn.evlight.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import cn.evlight.domain.activity.model.entity.ActivityEntity;
import cn.evlight.domain.activity.model.entity.RaffleActivityPartakeEntity;
import cn.evlight.domain.activity.model.entity.UserRaffleOrderEntity;
import cn.evlight.domain.activity.repository.IActivityRepository;
import cn.evlight.domain.activity.service.IRaffleActivityPartake;
import cn.evlight.domain.activity.service.partake.chain.IPartakeCheckChain;
import cn.evlight.domain.activity.service.partake.chain.factory.DefaultPartakeCheckChainFactory;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * @Description: 抽奖活动参与抽象类
 * @Author: evlight
 * @Date: 2024/6/6
 */

@Slf4j
public abstract class AbstractRaffleActivityPartake implements IRaffleActivityPartake {

    protected final DefaultPartakeCheckChainFactory defaultPartakeCheckChainFactory;
    protected final IActivityRepository activityRepository;

    public AbstractRaffleActivityPartake(DefaultPartakeCheckChainFactory defaultPartakeCheckChainFactory, IActivityRepository activityRepository) {
        this.defaultPartakeCheckChainFactory = defaultPartakeCheckChainFactory;
        this.activityRepository = activityRepository;
    }


    @Override
    public UserRaffleOrderEntity createPartakeOrder(RaffleActivityPartakeEntity raffleActivityPartakeEntity) {
        Long activityId = raffleActivityPartakeEntity.getActivityId();
        String userId = raffleActivityPartakeEntity.getUserId();
        LocalDateTime currentTime = LocalDateTime.now();
        //校验链校验
        ActivityEntity activityEntity = activityRepository.queryRaffleActivityByActivityId(activityId);
        IPartakeCheckChain partakeCheckChain = defaultPartakeCheckChainFactory.openCheckChain();
        partakeCheckChain.doCheck(activityEntity);
        //查询是否有未使用的抽奖单
        UserRaffleOrderEntity userRaffleOrderEntity = activityRepository.queryUnUsedRaffleOrder(activityId, userId);
        if(userRaffleOrderEntity != null){
            log.info("存在未使用的抽奖单");
            return userRaffleOrderEntity;
        }
        //校验用户额度
        CreatePartakeOrderAggregate createPartakeOrderAggregate = checkAccount(activityId, userId, currentTime);
        //构建订单
        userRaffleOrderEntity = buildUserRaffleOrder(activityId, userId, currentTime, activityEntity);
        //构建聚合对象
        createPartakeOrderAggregate.setUserRaffleOrderEntity(userRaffleOrderEntity);
        //保存聚合对象
        activityRepository.savePartakeOrderAggregate(createPartakeOrderAggregate);
        return userRaffleOrderEntity;
    }

    protected abstract UserRaffleOrderEntity buildUserRaffleOrder(Long activityId, String userId, LocalDateTime currentTime, ActivityEntity activityEntity);


    protected abstract CreatePartakeOrderAggregate checkAccount(Long activityId, String userId, LocalDateTime currentTime);

}
