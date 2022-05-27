package com.ctgu.service.impl;

import com.ctgu.model.BO.ResultMsgBO;
import com.ctgu.service.ITaskService;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.*;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
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
    private HistoryService historyService;

    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public ResultMsgBO getMyTask(String username) {
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
    public ResultMsgBO getHisTask(String username) {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(username)
                .finished()
                .list();
        List<Map<String,Object>> tasks = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(o->{
                    Map<String,Object> map =  new HashMap<>();
                    map.put("name",o.getName());
                    map.put("taskId",o.getId());
                    map.put("processId",o.getProcessInstanceId());
                    map.put("assignee",o.getAssignee());
                    map.put("createTime",sdf.format(o.getCreateTime()));
                    map.put("endTime",sdf.format(o.getEndTime()));
                    map.put("duration",o.getDurationInMillis());
                    tasks.add(map);
                });
        return new ResultMsgBO(0,"ok",tasks);
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
    public ResultMsgBO taskEnd() {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .finished()
                .list();
        List<Map<String,Object>> tasks = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(o->{
                    Map<String,Object> map =  new HashMap<>();
                    map.put("taskId",o.getId());
                    map.put("processId",o.getProcessInstanceId());
                    map.put("name",o.getName());
                    map.put("createTime",sdf.format(o.getCreateTime()));
                    map.put("endTime",sdf.format(o.getEndTime()));
                    map.put("formKey",o.getFormKey());
                    tasks.add(map);
                });
        return new ResultMsgBO(0,"ok",tasks);
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
