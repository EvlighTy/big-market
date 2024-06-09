package cn.evlight.test.domain.rebate;

import cn.evlight.domain.rebate.model.entity.BehaviorEntity;
import cn.evlight.domain.rebate.model.valobj.BehaviorTypeVO;
import cn.evlight.domain.rebate.service.IBehaviorRebateService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @Description: 返利行为测试
 * @Author: evlight
 * @Date: 2024/6/9
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RebateTest {

    @Autowired
    private IBehaviorRebateService behaviorRebateService;

    @Test
    public void test31(){
        List<String> orderIds = behaviorRebateService.createOrder(BehaviorEntity.builder()
                .userId("evlight")
                .behaviorTypeVO(BehaviorTypeVO.SIGN)
                .outBizId("20240614")
                .build());
        log.info("test31 测试结果:{}", orderIds);
    }

}
