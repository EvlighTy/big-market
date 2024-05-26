package cn.evlight.test.domain;

import cn.evlight.domain.strategy.model.entity.RaffleParamEntity;
import cn.evlight.domain.strategy.model.entity.RaffleResultEntity;
import cn.evlight.domain.strategy.service.IRaffleStrategy;
import cn.evlight.domain.strategy.service.armory.IManagerStrategyArmory;
import cn.evlight.domain.strategy.service.armory.IUserStrategyArmory;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description: 策略库测试
 * @Author: evlight
 * @Date: 2024/5/25
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyArmoryTest {

    @Autowired
    private IUserStrategyArmory userStrategyArmory;

    @Autowired
    private IManagerStrategyArmory managerStrategyArmory;

    @Autowired
    private IRaffleStrategy raffleStrategy;

    @Test
    public void test1(){
        managerStrategyArmory.generateStrategyRandomMap(100001L);
    }

    @Test
    public void test2(){
        for (int i = 0; i < 100; i++) {
            log.info(userStrategyArmory.getRandomAwardId(100001L + ":4000").toString());
        }
    }

    @Test
    public void test3(){
        RaffleParamEntity raffleParamEntity = RaffleParamEntity.builder()
                .strategyId(100001L)
                .userId(15L)
                .build();
        RaffleResultEntity raffleResultEntity = raffleStrategy.doRaffle(raffleParamEntity);
        System.err.println(JSON.toJSONString(raffleResultEntity));
    }

}
