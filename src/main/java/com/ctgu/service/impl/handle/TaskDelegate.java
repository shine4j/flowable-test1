package com.ctgu.service.impl.handle;

import com.ctgu.model.BO.TaskHandleBO;
import com.ctgu.service.TaskBaseHandle;
import org.flowable.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author beck_guo
 * @create 2022/5/24 9:56
 * @description
 */
@Service("taskDelegate")
public class TaskDelegate extends TaskBaseHandle {

    @Autowired
    private TaskService taskService;

    @Override
    public void handle(TaskHandleBO model) {
        taskService.delegateTask(model.getTaskId(), model.getAssign());
    }
}
