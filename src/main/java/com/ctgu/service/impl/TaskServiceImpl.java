package com.ctgu.service.impl;

import com.ctgu.BO.ResultMsgBO;
import com.ctgu.service.ITaskService;
import com.ctgu.util.TaskUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Autowired
    private HistoryService historyService;

    @Override
    public ResultMsgBO getTask() {
        List<Task> list = taskService.createTaskQuery().list();
        List<Map<String,Object>> tasks = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(o->{
                    Map<String,Object> map =  new HashMap<>();
                    map.put("id",o.getId());
                    map.put("processId",o.getProcessInstanceId());
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
        return new ResultMsgBO(0,"ok",null);
    }

    @Override
    public ResultMsgBO doDelegateTask(String taskId, String userId) {
        taskService.delegateTask(taskId, userId);
        return new ResultMsgBO(0,"ok",null);
    }

    @Override
    public ResultMsgBO getHisTask() {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .finished()
                .list();
        List<Map<String,Object>> tasks = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(o->{
                    Map<String,Object> map =  new HashMap<>();
                    map.put("name",o.getName());
                    map.put("assignee",o.getAssignee());
                    map.put("createTime",o.getCreateTime());
                    map.put("endTime",o.getEndTime());
                    map.put("duration",o.getDurationInMillis());
                    tasks.add(map);
                });
        return new ResultMsgBO(0,"ok",tasks);
    }
}
