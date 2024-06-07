package cn.evlight.infrastructure.persistent.repository;

import cn.evlight.domain.task.model.entity.TaskEntity;
import cn.evlight.domain.task.repository.ITaskRepository;
import cn.evlight.infrastructure.event.EventPublisher;
import cn.evlight.infrastructure.persistent.dao.TaskMapper;
import cn.evlight.infrastructure.persistent.po.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 任务服务仓库实现类
 * @Author: evlight
 * @Date: 2024/6/7
 */

@Repository
public class TaskRepository implements ITaskRepository {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private EventPublisher eventPublisher;

    @Override
    public List<TaskEntity> queryUnSendMessageTaskList() {
        List<Task> tasks = taskMapper.queryUnSendMessageTaskList();
        List<TaskEntity> taskEntities = new ArrayList<>(tasks.size());
        for (Task task : tasks) {
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setUserId(task.getUserId());
            taskEntity.setTopic(task.getTopic());
            taskEntity.setMessageId(task.getMessageId());
            taskEntity.setMessage(task.getMessage());
            taskEntities.add(taskEntity);
        }
        return taskEntities;
    }

    @Override
    public void sendMessage(TaskEntity taskEntity) {
        eventPublisher.publish(taskEntity.getTopic(), taskEntity.getMessage());
    }

    @Override
    public void updateAfterCompleted(String userId, String messageId) {
        Task task = new Task();
        task.setUserId(userId);
        task.setMessageId(messageId);
        taskMapper.updateAfterCompleted(task);
    }

    @Override
    public void updateAfterFailed(String userId, String messageId) {
        Task task = new Task();
        task.setUserId(userId);
        task.setMessageId(messageId);
        taskMapper.updateAfterFailed(task);
    }
}
