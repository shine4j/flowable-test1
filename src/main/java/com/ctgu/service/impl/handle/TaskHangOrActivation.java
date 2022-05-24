package com.ctgu.service.impl.handle;

import com.ctgu.model.BO.TaskHandleBO;
import com.ctgu.service.TaskBaseHandle;
import com.ctgu.util.FlowProcessDiagramGenerator;
import com.ctgu.util.TaskUtils;
import org.flowable.engine.*;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author beck_guo
 * @create 2022/5/24 9:56
 * @description 委派任务还会回来,转办任务不会回来
 */
@Service("taskHangOrActivation")
public class TaskHangOrActivation extends TaskBaseHandle {

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    ManagementService managementService;

    @Override
    public void handle(TaskHandleBO model) {
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(model.getProccessId())
                .singleResult();
        Map<String, Object> map = new HashMap<>();
        if(instance.isSuspended()){
            runtimeService.activateProcessInstanceById(model.getProccessId());
            map.put("status","suspended");
        }else{
            runtimeService.suspendProcessInstanceById(model.getProccessId());
            map.put("status","activate");
        }
    }
}
