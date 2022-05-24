package com.ctgu.service.impl.handle;

import com.ctgu.model.BO.TaskHandleBO;
import com.ctgu.service.TaskBaseHandle;
import com.ctgu.util.TaskUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author beck_guo
 * @create 2022/5/24 9:52
 * @description
 */
@Service("taskAnyNode")
public class TaskAnyNode extends TaskBaseHandle {

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    private TaskUtils taskUtils;



    @Override
    public void handle(TaskHandleBO model) {
        Task task =  taskService.createTaskQuery().taskId(model.getTaskId()).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        List<Process> processes = bpmnModel.getProcesses();
        FlowNode distActivity = null;
        for (Process process : processes) {
            FlowElement flowElement = process.getFlowElementMap().get(model.getActId());
            if (flowElement != null) {
                distActivity = (FlowNode) flowElement;
                break;
            }
        }
        Map<String, Object> map = new HashMap();
        if (distActivity != null) {
            if ("提交人".equals(distActivity.getName())) {
                ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
                map.put("initiator", processInstance.getStartUserId());
            }
        }
        List<String> currentExecutionIds = new ArrayList<>();
        List<Execution> executions = runtimeService.createExecutionQuery().parentId(task.getProcessInstanceId()).list();
        for (Execution execution : executions) {
            currentExecutionIds.add(execution.getId());
        }
        taskUtils.deleteActivity(model.getActId(),task.getProcessInstanceId());
        runtimeService.createChangeActivityStateBuilder()
                .processVariables(map)
                .moveExecutionsToSingleActivityId(currentExecutionIds, model.getActId())
                .changeState();
    }
}
