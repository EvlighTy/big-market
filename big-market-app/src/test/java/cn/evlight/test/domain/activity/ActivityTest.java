package cn.evlight.test.domain.activity;

import cn.evlight.domain.activity.model.entity.RaffleActivityPartakeEntity;
import cn.evlight.domain.activity.model.entity.RaffleActivityQuotaEntity;
import cn.evlight.domain.activity.model.entity.UserRaffleOrderEntity;
import cn.evlight.domain.activity.service.IRaffleActivityPartake;
import cn.evlight.domain.activity.service.IRaffleActivityQuota;
import cn.evlight.domain.activity.service.quota.armory.IActivityArmory;
import cn.evlight.domain.award.model.entity.UserAwardRecordEntity;
import cn.evlight.domain.award.model.valobj.AwardStateVO;
import cn.evlight.domain.award.service.IAwardService;
import cn.evlight.types.exception.AppException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;

/**
 * @Description: 活动测试类
 * @Author: evlight
 * @Date: 2024/6/1
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivityTest {

    @Autowired
    private IRaffleActivityQuota raffleActivityQuota;

    @Autowired
    private IActivityArmory activityArmory;

    @Autowired
    private IRaffleActivityPartake raffleActivityPartake;

    @Autowired
    private IAwardService awardService;



    @Test
    public void test22(){
        String orderId = raffleActivityQuota.createQuotaOrder(RaffleActivityQuotaEntity.builder()
                .sku(9011L)
                .userId("evlight")
                .outBusinessNo("123")
                .build());
        log.info("test22测试结果:{}", orderId);
    }

    @Test
    public void test23() throws InterruptedException {
        activityArmory.assembleActivitySkuBySku(9011L);
        for (int i = 0; i < 10; i++) {
            try {
                String orderId = raffleActivityQuota.createQuotaOrder(RaffleActivityQuotaEntity.builder()
                        .sku(9011L)
                        .userId("evlight")
                        .outBusinessNo(RandomStringUtils.randomNumeric(12))
                        .build());
                log.info("test23测试结果：{}", orderId);
            } catch (AppException e) {
                e.printStackTrace();
            }
        }
        new CountDownLatch(1).await();
    }

    @Test
    public void test24(){
        UserRaffleOrderEntity result = raffleActivityPartake.createPartakeOrder(RaffleActivityPartakeEntity.builder()
                .activityId(100301L)
                .userId("evlight")
                .build());
        log.info("test24测试结果:{}", JSON.toJSONString(result));
    }

    @Test
    public void test26() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            UserAwardRecordEntity userAwardRecordEntity = new UserAwardRecordEntity();
            userAwardRecordEntity.setUserId("evlight");
            userAwardRecordEntity.setActivityId(100301L);
            userAwardRecordEntity.setStrategyId(100006L);
            userAwardRecordEntity.setOrderId(RandomStringUtils.randomNumeric(12));
            userAwardRecordEntity.setAwardId(101);
            userAwardRecordEntity.setAwardTitle("OpenAI 增加使用次数");
            userAwardRecordEntity.setAwardTime(LocalDateTime.now());
            userAwardRecordEntity.setAwardState(AwardStateVO.create);
            awardService.saveUserAwardRecord(userAwardRecordEntity);
            Thread.sleep(500);
        }
        new CountDownLatch(1).await();
    }

}
