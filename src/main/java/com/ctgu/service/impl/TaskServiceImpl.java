package com.ctgu.service.impl;

import com.ctgu.BO.ResultMsgBO;
import com.ctgu.service.ITaskService;
import com.ctgu.util.TaskUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author beck_guo
 * @create 2022/4/29 15:17
 * @description
 */
@Service
public class TaskServiceImpl implements ITaskService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    private TaskUtils taskUtils;

    @Override
    public ResultMsgBO getTask() {
        List<Task> list = taskService.createTaskQuery().list();
        List<Map<String,Object>> tasks = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(o->{
                    Map<String,Object> map =  new HashMap<>();
                    map.put("id",o.getId());
                    map.put("name",o.getName());
                    map.put("createTime",o.getCreateTime());
                    map.put("formKey",o.getFormKey());
                    tasks.add(map);
                });
        return new ResultMsgBO(0,"ok",tasks);
    }

    @Override
    public ResultMsgBO doComplete(String taskId) {
        taskService.complete(taskId);
        return new ResultMsgBO(0,"ok",null);
    }



    @Override
    public ResultMsgBO getBackNodes(String processInstanceId) {
        List<ActivityInstance> userTask = runtimeService.createActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .activityType("userTask")
                .finished()
                .list();
        List<ActivityInstance> list = userTask.stream().filter(taskUtils.distinctByKey(ActivityInstance::getActivityId)).sorted(Comparator.comparing(ActivityInstance::getEndTime)).collect(Collectors.toList());
        List<Map<String,Object>> nodes = new ArrayList<>();
       Optional.ofNullable(list).orElse(new ArrayList<>())
               .forEach(o->{
                   Map<String,Object> map =  new HashMap<>();
                   map.put("activityId",o.getActivityId());
                   map.put("name",o.getActivityName());
                   nodes.add(map);
               });
        return new ResultMsgBO(0,"ok",nodes);
    }

    @Override
    public ResultMsgBO doBack(String taskId, String distFlowElementId) {
        Task task =  taskService.createTaskQuery().taskId(taskId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        List<Process> processes = bpmnModel.getProcesses();
        FlowNode distActivity = null;
        for (Process process : processes) {
            FlowElement flowElement = process.getFlowElementMap().get(distFlowElementId);
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
        taskUtils.deleteActivity(distFlowElementId,task.getProcessInstanceId());
        runtimeService.createChangeActivityStateBuilder()
                .processVariables(map)
                .moveExecutionsToSingleActivityId(currentExecutionIds, distFlowElementId)
                .changeState();
        return null;
    }
}
