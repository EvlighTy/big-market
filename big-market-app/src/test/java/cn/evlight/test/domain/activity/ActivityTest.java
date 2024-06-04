package cn.evlight.test.domain.activity;

import cn.evlight.domain.activity.model.entity.SkuRechargeEntity;
import cn.evlight.domain.activity.service.IRaffleActivity;
import cn.evlight.domain.activity.service.armory.IActivityArmory;
import cn.evlight.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
    private IRaffleActivity raffleActivity;

    @Autowired
    private IActivityArmory activityArmory;

    @Test
    public void test22(){
        SkuRechargeEntity skuRechargeEntity = new SkuRechargeEntity();
        skuRechargeEntity.setUserId("evlight");
        skuRechargeEntity.setSku(9011L);
        skuRechargeEntity.setOutBusinessNo("1234567");
        String orderId = raffleActivity.createSkuRechargeOrder(skuRechargeEntity);
        log.info("test22测试结果:{}", orderId);
    }

    @Test
    public void test23() throws InterruptedException {
        activityArmory.assembleActivitySku(9011L);
        for (int i = 0; i < 10; i++) {
            try {
                SkuRechargeEntity skuRechargeEntity = new SkuRechargeEntity();
                skuRechargeEntity.setUserId("evlight");
                skuRechargeEntity.setSku(9011L);
                skuRechargeEntity.setOutBusinessNo(RandomStringUtils.randomNumeric(12));
                String orderId = raffleActivity.createSkuRechargeOrder(skuRechargeEntity);
                log.info("测试结果：{}", orderId);
            } catch (AppException e) {
                e.printStackTrace();
            }
        }

        new CountDownLatch(1).await();
    }


}
