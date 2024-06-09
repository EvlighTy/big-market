package cn.evlight.domain.activity.service;

import cn.evlight.domain.activity.model.entity.RaffleActivityPartakeEntity;
import cn.evlight.domain.activity.model.entity.UserRaffleOrderEntity;

/**
 * @Description: 抽奖活动参与接口
 * @Author: evlight
 * @Date: 2024/6/6
 */
public interface IRaffleActivityPartake {

    /**
    * @Description: 创建抽奖单
    * @Param: [raffleActivityPartakeEntity]
    * @return:
    * @Date: 2024/6/9
    */
    UserRaffleOrderEntity createPartakeOrder(RaffleActivityPartakeEntity raffleActivityPartakeEntity);

}
