package cn.evlight.trigger.job;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.evlight.domain.task.model.entity.TaskEntity;
import cn.evlight.domain.task.service.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Description: 发送中奖消息到MQ任务
 * @Author: evlight
 * @Date: 2024/6/7
 */

@Slf4j
@Component
public class SendAwardMessageJob {

    @Autowired
    private ITaskService taskService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private IDBRouterStrategy dbRouter;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec() {
        log.info("[Scheduled Task]-[发送中奖消息到MQ] 开始执行...");
        try{
            int dbCount = dbRouter.dbCount();
            for (int i = 1; i <= dbCount; i++) {
                int current = i;
                executor.execute(() -> {
                    try {
                        dbRouter.setDBKey(current);
                        dbRouter.setTBKey(0);
                        List<TaskEntity> taskEntities = taskService.queryUnSendMessageTaskList();
                        if (taskEntities == null || taskEntities.isEmpty()){
                            log.info("[Scheduled Task]-[发送中奖消息到MQ] 未查询到未发送的消息");
                            return;
                        }
                        for (TaskEntity taskEntity : taskEntities) {
                            executor.execute(() -> {
                                try {
                                    taskService.sendMessage(taskEntity);
                                    taskService.updateAfterCompleted(taskEntity.getUserId(), taskEntity.getMessageId());
                                }catch (Exception e){
                                    log.info("定时任务-[发送中奖消息到MQ] 发送到MQ失败", e);
                                    taskService.updateAfterFailed(taskEntity.getUserId(), taskEntity.getMessageId());
                                }
                            });
                        }
                    }finally {
                        dbRouter.clear();
                    }
                });
            }
        }catch (Exception e){
            log.info("[发送中奖消息到MQ] 查询库表失败");
        }

    }
}
