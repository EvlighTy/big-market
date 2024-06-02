package cn.evlight.test.domain.activity;

import cn.evlight.domain.activity.model.entity.SkuRechargeEntity;
import cn.evlight.domain.activity.service.IRaffleActivity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

    @Test
    public void test22(){
        SkuRechargeEntity skuRechargeEntity = new SkuRechargeEntity();
        skuRechargeEntity.setUserId("evlight");
        skuRechargeEntity.setSku(9011L);
        skuRechargeEntity.setOutBusinessNo("1234567");
        String orderId = raffleActivity.createSkuRechargeOrder(skuRechargeEntity);
        log.info("test22测试结果:{}", orderId);
    }

}
