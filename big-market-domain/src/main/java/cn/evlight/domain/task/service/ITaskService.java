package cn.evlight.domain.task.service;

import cn.evlight.domain.task.model.entity.TaskEntity;

import java.util.List;

/**
 * @Description: 任务服务接口
 * @Author: evlight
 * @Date: 2024/6/7
 */
public interface ITaskService {

    List<TaskEntity> queryUnSendMessageTaskList();

    void sendMessage(TaskEntity taskEntity);

    void updateAfterCompleted(String userId, String messageId);

    void updateAfterFailed(String userId, String messageId);

}
