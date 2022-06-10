package com.ctgu.service.impl;

import com.ctgu.dao.TaskDao;
import com.ctgu.model.BO.pager.PageQueryBO;
import com.ctgu.model.BO.ResultMsgBO;
import com.ctgu.model.BO.TaskQueryBO;
import com.ctgu.model.BO.pager.PagerModel;
import com.ctgu.service.ITaskService;
import com.ctgu.util.TaskUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.*;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.ProcessInstance;
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
    private RuntimeService runtimeService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    private TaskUtils taskUtils;

    @Autowired
    private TaskDao taskDao;

    @Override
    public PagerModel<Map> getMyTask(TaskQueryBO params, PageQueryBO query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        Page<Map> myTask = taskDao.getMyTask(params);
        return new PagerModel<Map>(myTask);
    }


    @Override
    public PagerModel<Map> getMyHisTask(TaskQueryBO params, PageQueryBO query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        Page<Map> myTask = taskDao.getMyHisTask(params);
        return new PagerModel<Map>(myTask);
    }

    @Override
    public PagerModel<Map> getMyStart(TaskQueryBO parms,PageQueryBO query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        Page<Map> myStart = taskDao.getMyStart(parms);
        return new PagerModel<Map>(myStart);
    }

    @Override
    public PagerModel<Map> taskIng(TaskQueryBO parms,PageQueryBO query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        Page<Map> taskIng = taskDao.taskIng(parms);
        return new PagerModel<Map>(taskIng);
    }

    @Override
    public PagerModel<Map> taskEnd(TaskQueryBO parms,PageQueryBO query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        Page<Map> taskEnd = taskDao.taskEnd(parms);
        return new PagerModel<Map>(taskEnd);
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

    @Override
    public ResultMsgBO getBackNodes(String processInstanceId) {
        List<ActivityInstance> userTask = runtimeService.createActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .activityType("userTask")
                .finished()
                .list();
        List<ActivityInstance> list = userTask.stream().filter(taskUtils.distinctByKey(ActivityInstance::getActivityId)).sorted(Comparator.comparing(ActivityInstance::getEndTime)).collect(Collectors.toList());
        List<Map<String, Object>> nodes = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(o -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("activityId", o.getActivityId());
                    map.put("name", o.getActivityName());
                    nodes.add(map);
                });
        return new ResultMsgBO(0, "ok", nodes);
    }

}
