package cn.evlight.domain.task.service;

import cn.evlight.domain.task.model.entity.TaskEntity;
import cn.evlight.domain.task.repository.ITaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 任务服务实现类
 * @Author: evlight
 * @Date: 2024/6/7
 */

@Service
public class TaskService implements ITaskService{

    @Autowired
    private ITaskRepository taskRepository;

    @Override
    public List<TaskEntity> queryUnSendMessageTaskList() {
        return taskRepository.queryUnSendMessageTaskList();
    }

    @Override
    public void sendMessage(TaskEntity taskEntity) {
        taskRepository.sendMessage(taskEntity);
    }

    @Override
    public void updateAfterCompleted(String userId, String messageId) {
        taskRepository.updateAfterCompleted(userId, messageId);
    }

    @Override
    public void updateAfterFailed(String userId, String messageId) {
        taskRepository.updateAfterFailed(userId, messageId);
    }
}
