package cn.evlight.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.evlight.infrastructure.persistent.po.Task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 任务表，发送MQ Mapper 接口
 * </p>
 *
 * @author evlight
 * @since 2024-06-06
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {

    void save(Task task);

    @DBRouter
    void updateAfterCompleted(Task task);

    @DBRouter
    void updateAfterFailed(Task task);

    List<Task> queryUnSendMessageTaskList();
}
