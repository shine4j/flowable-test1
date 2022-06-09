package com.ctgu.service.impl;

import com.alibaba.fastjson.JSON;
import com.ctgu.dao.ProcessDao;
import com.ctgu.model.BO.ProcessQueryBO;
import com.ctgu.model.BO.ResultMsgBO;
import com.ctgu.model.BO.pager.PageQueryBO;
import com.ctgu.model.BO.pager.PagerModel;
import com.ctgu.model.PO.RolePO;
import com.ctgu.cmd.TaskApplyUserCmd;
import com.ctgu.service.IProcessService;
import com.ctgu.util.FlowProcessDiagramGenerator;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.impl.util.IoUtil;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
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
    private FlowProcessDiagramGenerator flowProcessDiagramGenerator;

    @Autowired
    ManagementService managementService;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ProcessDao processDao;

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
    public ResultMsgBO applyNodes(String processInstanceId) {
        List<Map> list = processDao.applyNodes(processInstanceId);
        List<Map<String, Object>> tasks = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(o -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("nodeName", o.get("nodeName"));
                    if (null!=o.get("assignee")) {
                        map.put("assign",o.get("assignee"));
                    } else {
                        List<RolePO> roles = managementService.executeCommand(new TaskApplyUserCmd(o.get("id").toString()));
                        map.put("assign", JSON.toJSONString(roles));
                    }
                    tasks.add(map);
                });
        return new ResultMsgBO(0, "ok", tasks);
    }

    @Override
    public PagerModel<Map> getNoFish(ProcessQueryBO params, PageQueryBO query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        Page<Map> taskIng = processDao.getNoFish(params);
        return new PagerModel<Map>(taskIng);
    }

    @Override
    public PagerModel<Map> getFish(ProcessQueryBO params, PageQueryBO query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        Page<Map> taskIng = processDao.getFish(params);
        return new PagerModel<Map>(taskIng);
    }


}
