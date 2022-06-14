package com.ctgu.service.impl.handle;

import com.ctgu.model.BO.TaskHandleBO;
import com.ctgu.service.TaskBaseHandle;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author beck_guo
 * @create 2022/5/24 9:56
 * @description
 */
@Service("taskTransferOrDelegate")
public class TaskTransferOrDelegate extends TaskBaseHandle {

    @Autowired
    private TaskService taskService;

    @Override
    public void handle(TaskHandleBO model) {
        TaskEntityImpl task = (TaskEntityImpl) taskService.createTaskQuery().taskId(model.getTaskId()).singleResult();
        task.setOwner(model.getComment().getUserId());
        task.setScopeType(model.getType());
        taskService.saveTask(task);
        taskService.delegateTask(model.getTaskId(), model.getAssign());
    }
}
