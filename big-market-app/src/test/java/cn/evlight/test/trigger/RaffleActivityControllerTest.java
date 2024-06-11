package cn.evlight.test.trigger;

import cn.evlight.api.IRaffleActivityService;
import cn.evlight.api.dto.request.RaffleActivityRequestDTO;
import cn.evlight.api.dto.response.RaffleActivityResponseDTO;
import cn.evlight.types.model.Response;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

/**
 * @Description: 活动抽奖接口测试
 * @Author: evlight
 * @Date: 2024/6/11
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleActivityControllerTest {

    @Resource
    private IRaffleActivityService raffleActivityService;

    @Test
    public void blacklist_user_raffle_test() throws InterruptedException {
        RaffleActivityRequestDTO request = new RaffleActivityRequestDTO();
        request.setActivityId(100301L);
        request.setUserId("user001");
        Response<RaffleActivityResponseDTO> response = raffleActivityService.activityRaffle(request);
        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));
        // 让程序挺住方便测试，也可以去掉
        new CountDownLatch(1).await();
    }

}
