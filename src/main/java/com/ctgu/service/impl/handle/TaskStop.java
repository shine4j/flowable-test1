package com.ctgu.service.impl.handle;

import com.ctgu.model.BO.TaskHandleBO;
import com.ctgu.service.TaskBaseHandle;
import com.ctgu.util.TaskUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author beck_guo
 * @create 2022/5/24 10:56
 * @description
 */
@Service("taskStop")
public class TaskStop extends TaskBaseHandle {

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected RepositoryService repositoryService;

    @Override
    public void handle(TaskHandleBO model) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(model.getProccessId()).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel((processInstance.getProcessDefinitionId()));
        Process process = bpmnModel.getMainProcess();
        List<EndEvent> endNodes = process.findFlowElementsOfType(EndEvent.class);
        String endId = endNodes.get(0).getId();
        //3、执行终止
        List<Execution> executions = runtimeService.createExecutionQuery().parentId(model.getProccessId()).list();
        List<String> executionIds = new ArrayList<>();
        executions.forEach(execution -> executionIds.add(execution.getId()));
        runtimeService.createChangeActivityStateBuilder()
                .moveExecutionsToSingleActivityId(executionIds, endId)
                .changeState();
    }
}
