package cn.evlight.test.domain.credit;

import cn.evlight.domain.credit.model.entity.CreditEntity;
import cn.evlight.domain.credit.model.valobj.TradeNameVO;
import cn.evlight.domain.credit.model.valobj.TradeTypeVO;
import cn.evlight.domain.credit.service.ICreditService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

/**
 * @Description: 积分领域测试
 * @Author: evlight
 * @Date: 2024/6/12
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CreditTest {

    @Resource
    private ICreditService creditService;

    @Test
    public void test_createOrder_forward() {
        creditService.createOrder(CreditEntity.builder()
                .userId("xiaofuge")
                .tradeName(TradeNameVO.REBATE)
                .tradeType(TradeTypeVO.FORWARD)
                .amount(new BigDecimal("10.19"))
                .outBusinessNo("100009909911")
                .build());
    }

    @Test
    public void test_createOrder_pay() throws InterruptedException {
        creditService.createOrder(CreditEntity.builder()
                .userId("evlight")
                .tradeName(TradeNameVO.CONVERT_SKU)
                .tradeType(TradeTypeVO.REVERSE)
                .amount(new BigDecimal("-1.68"))
                .outBusinessNo("70009240609007")
                .build());

        new CountDownLatch(1).await();
    }

}
