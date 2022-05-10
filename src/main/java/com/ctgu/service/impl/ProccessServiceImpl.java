package com.ctgu.service.impl;

import com.alibaba.fastjson.JSON;
import com.ctgu.BO.ResultMsgBO;
import com.ctgu.PO.RolePO;
import com.ctgu.cmd.TaskApplyUserCmd;
import com.ctgu.service.IProcessService;
import com.ctgu.util.FlowProcessDiagramGenerator;
import com.ctgu.util.TaskUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.Process;
import org.flowable.common.engine.impl.util.IoUtil;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author beck_guo
 * @create 2022/4/29 15:33
 * @description
 */
@Service
public class ProccessServiceImpl implements IProcessService {


    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    private TaskUtils taskUtils;

    @Autowired
    private FlowProcessDiagramGenerator flowProcessDiagramGenerator;

    @Autowired
    private IdentityService identityService;

    @Autowired
    ManagementService managementService;

    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");



    @Override
    public void getImage(String processInstanceId, HttpServletResponse response) throws IOException {
        //1.获取当前的流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        String processDefinitionId = null;
        List<String> activeActivityIds = new ArrayList<>();
        List<String> highLightedFlows = new ArrayList<>();
        //2.获取所有的历史轨迹线对象
        List<HistoricActivityInstance> historicSquenceFlows = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).activityType(BpmnXMLConstants.ELEMENT_SEQUENCE_FLOW).list();
        historicSquenceFlows.forEach(historicActivityInstance -> highLightedFlows.add(historicActivityInstance.getActivityId()));
        //3. 获取流程定义id和高亮的节点id
        if (processInstance != null) {
            //3.1. 正在运行的流程实例
            processDefinitionId = processInstance.getProcessDefinitionId();
            activeActivityIds = runtimeService.getActiveActivityIds(processInstanceId);
        } else {
            //3.2. 已经结束的流程实例
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            processDefinitionId = historicProcessInstance.getProcessDefinitionId();
            //3.3. 获取结束节点列表
            List<HistoricActivityInstance> historicEnds = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId).activityType(BpmnXMLConstants.ELEMENT_EVENT_END).list();
            List<String> finalActiveActivityIds = activeActivityIds;
            historicEnds.forEach(historicActivityInstance -> finalActiveActivityIds.add(historicActivityInstance.getActivityId()));
        }
        //4. 获取bpmnModel对象
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        //5. 生成图片流
        InputStream inputStream = flowProcessDiagramGenerator.generateDiagram(bpmnModel, activeActivityIds, highLightedFlows);
        //6. 转化成byte便于网络传输
        byte[] b = IoUtil.readInputStream(inputStream, "image inputStream name");
        response.setHeader("Content-Type", "image/png");
        response.getOutputStream().write(b);
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
    public ResultMsgBO doStopProcess(String processInstanceId) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (processInstance != null) {
            List<EndEvent> endNodes = null;
            BpmnModel bpmnModel = repositoryService.getBpmnModel((processInstance.getProcessDefinitionId()));
            if (bpmnModel != null) {
                Process process = bpmnModel.getMainProcess();
                endNodes = process.findFlowElementsOfType(EndEvent.class);
            }
            String endId = endNodes.get(0).getId();
            //3、执行终止
            List<Execution> executions = runtimeService.createExecutionQuery().parentId(processInstanceId).list();
            List<String> executionIds = new ArrayList<>();
            executions.forEach(execution -> executionIds.add(execution.getId()));
            runtimeService.createChangeActivityStateBuilder()
                    .moveExecutionsToSingleActivityId(executionIds, endId)
                    .changeState();
        }
        return new ResultMsgBO(0,"ok",null);
    }

    @Override
    public ResultMsgBO getNoFish() {
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().unfinished().list();
        List<Map<String,Object>> process = new ArrayList<>();
        list.forEach(s->{
            Map<String,Object> map =  new HashMap<>();
            map.put("processDefinitionName",s.getProcessDefinitionName());
            map.put("porcessId",s.getId());
            map.put("startUser",s.getStartUserId());
            process.add(map);
        });
       return new ResultMsgBO(0,"ok",process);
    }

    @Override
    public ResultMsgBO getStart() {
        List<HistoricProcessInstance> list = historyService
                .createHistoricProcessInstanceQuery()
                .list();
        List<Map<String,Object>> process = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(o->{
                    Map<String,Object> map =  new HashMap<>();
                    map.put("processDefinitionName",o.getProcessDefinitionName());
                    map.put("processId",o.getId());
                    map.put("startTime",sdf.format(o.getStartTime()));
                    process.add(map);
                });
        return new ResultMsgBO(0,"ok",process);
    }

    @Override
    public ResultMsgBO startByKey(String key) {
        identityService.setAuthenticatedUserId("beck_guo");
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("initiator", "");
        varMap.put("skip", true);
        varMap.put("_FLOWABLE_SKIP_EXPRESSION_ENABLED", true);
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(key,varMap);
        return new ResultMsgBO(0,"ok",null);
    }

    @Override
    public ResultMsgBO applyNodes(String processInstanceId) {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .unfinished()
                .list();
        List<Map<String, Object>> tasks = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(o -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("nodeName", o.getName());
                    if (StringUtils.isBlank(o.getAssignee())) {
                        List<RolePO> roles = managementService.executeCommand(new TaskApplyUserCmd(o.getId()));
                        map.put("assign", JSON.toJSONString(roles));
                    } else {
                        map.put("assign", o.getAssignee());
                    }
                    tasks.add(map);
                });
        return new ResultMsgBO(0, "ok", tasks);
    }

}
