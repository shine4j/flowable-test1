package com.ctgu.service.impl;

import com.ctgu.BO.ResultMsgBO;
import com.ctgu.BO.TaskHandleBO;
import com.ctgu.service.ITaskService;
import com.ctgu.util.TaskUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.*;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
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

    @Autowired
    private ManagementService managementService;



    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public ResultMsgBO getTask(String username) {
        List<Task> list = taskService.createTaskQuery()
                .taskCandidateOrAssigned(username).list();
        List<Map<String,Object>> tasks = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(o->{
                    Map<String,Object> map =  new HashMap<>();
                    map.put("taskId",o.getId());
                    map.put("processId",o.getProcessInstanceId());
                    map.put("name",o.getName());
                    map.put("createTime",sdf.format(o.getCreateTime()));
                    map.put("formKey",o.getFormKey());
                    tasks.add(map);
                });
        return new ResultMsgBO(0,"ok",tasks);
    }

    @Override
    public ResultMsgBO doComplete(TaskHandleBO model) {
        TaskEntity task = (TaskEntity) taskService.createTaskQuery()
                .taskId(model.getTaskId())
                .singleResult();
        taskService.complete(model.getTaskId());
        String parentTaskId = task.getParentTaskId();
        if(StringUtils.isNotEmpty(parentTaskId)){
            String tableName = managementService.getTableName(TaskEntity.class);
            String sql="select count(1) from "+tableName+" where parent_task_id_=#{parentTaskId}";
            long subTaskCount=taskService.createNativeTaskQuery()
                    .sql(sql).parameter("parentTaskId",parentTaskId).count();
            if(subTaskCount==0){
                taskService.resolveTask(parentTaskId);
                Task pTask = taskService.createTaskQuery().taskId(parentTaskId).singleResult();
                if("after".equals(pTask.getScopeType())){
                    taskService.complete(pTask.getId());
                }
            }
        }

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
                    map.put("createTime",sdf.format(o.getCreateTime()));
                    map.put("endTime",sdf.format(o.getEndTime()));
                    map.put("duration",o.getDurationInMillis());
                    tasks.add(map);
                });
        return new ResultMsgBO(0,"ok",tasks);
    }

    @Override
    public ResultMsgBO addSign(TaskHandleBO model) {
        taskUtils.creatSubTask(model);
        return new ResultMsgBO(0,"ok",null);
    }

    @Override
    public ResultMsgBO taskIng() {
        List<Task> list = taskService.createTaskQuery()
                .list();
        List<Map<String,Object>> tasks = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(o->{
                    Map<String,Object> map =  new HashMap<>();
                    map.put("taskId",o.getId());
                    map.put("processId",o.getProcessInstanceId());
                    map.put("name",o.getName());
                    map.put("createTime",sdf.format(o.getCreateTime()));
                    map.put("formKey",o.getFormKey());
                    tasks.add(map);
                });
        return new ResultMsgBO(0,"ok",tasks);
    }

    @Override
    public ResultMsgBO setAssignee(TaskHandleBO model) {
        taskService.setAssignee(model.getTaskId(),model.getAssign());
        return new ResultMsgBO(0,"ok",null);
    }

    @Override
    public ResultMsgBO getTaskAllNode(String processId) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
        BpmnModel bpmn = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        List<Process> processes = bpmn.getProcesses();
        List<Map<String,Object>> tasks = new ArrayList<>();
        processes.forEach(o->{
            Collection<FlowElement> flowElements = o.getFlowElements();
            flowElements.forEach(k->{
                if(k instanceof UserTask){
                    Map<String,Object> map =  new HashMap<>();
                    map.put("id",k.getId());
                    map.put("taskName",k.getName());
                    tasks.add(map);
                }
            });
        });
        return new ResultMsgBO(0,"ok",tasks);
    }


}
