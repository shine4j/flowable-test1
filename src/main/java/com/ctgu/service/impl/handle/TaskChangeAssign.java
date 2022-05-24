package com.ctgu.service.impl.handle;

import com.ctgu.model.BO.TaskHandleBO;
import com.ctgu.service.TaskBaseHandle;
import com.ctgu.util.TaskUtils;
import org.flowable.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author beck_guo
 * @create 2022/5/24 10:56
 * @description
 */
@Service("taskChangeAssign")
public class TaskChangeAssign extends TaskBaseHandle {

    @Autowired
    private TaskService taskService;

    @Override
    public void handle(TaskHandleBO model) {
        taskService.setAssignee(model.getTaskId(),model.getAssign());
    }
}
