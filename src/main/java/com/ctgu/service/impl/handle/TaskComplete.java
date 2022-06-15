package com.ctgu.service.impl.handle;

import com.ctgu.model.BO.TaskHandleBO;
import com.ctgu.service.TaskBaseHandle;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.*;
import org.flowable.task.api.Task;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author beck_guo
 * @create 2022/5/24 9:38
 * @description
 */
@Service("taskComplete")
public class TaskComplete extends TaskBaseHandle {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ManagementService managementService;

    @Override
    public void handle(TaskHandleBO model) {
        TaskEntity task = (TaskEntity) taskService.createTaskQuery()
                .taskId(model.getTaskId())
                .singleResult();
        taskService.complete(model.getTaskId());
        checkParentTask(task);
    }

    public void checkParentTask(Task model) {
        String parentTaskId = model.getParentTaskId();
        if (StringUtils.isNotEmpty(parentTaskId)) {
            String tableName = managementService.getTableName(TaskEntity.class);
            String sql = "select count(1) from " + tableName + " where parent_task_id_=#{parentTaskId}";
            long subTaskCount = taskService.createNativeTaskQuery()
                    .sql(sql).parameter("parentTaskId", parentTaskId).count();
            if (subTaskCount == 0) {
                taskService.resolveTask(parentTaskId);
                Task pTask = taskService.createTaskQuery().taskId(parentTaskId).singleResult();
                if ("after".equals(pTask.getScopeType())) {
                    taskService.complete(pTask.getId());
                }
                checkParentTask(pTask);
            }
        }
    }
}
