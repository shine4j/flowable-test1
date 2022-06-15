package com.ctgu.service.impl.handle;

import com.ctgu.model.BO.TaskHandleBO;
import com.ctgu.service.TaskBaseHandle;
import com.ctgu.util.TaskUtils;
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
    private TaskUtils taskUtils;

    @Override
    public void handle(TaskHandleBO model) {
        taskUtils.creatSubTask(model);
    }
}
