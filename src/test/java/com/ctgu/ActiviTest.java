package com.ctgu;


import com.alibaba.fastjson.JSON;
import com.ctgu.dao.ProcessDao;
import com.ctgu.model.PO.RolePO;
import com.ctgu.cmd.ApplyUserCmd;
import com.ctgu.dao.HisFlowableActinstDao;
import com.ctgu.dao.RunFlowableActinstDao;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.persistence.entity.ActivityInstanceEntity;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.flowable.ui.modeler.domain.AbstractModel;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @Author beck_guo
 * @create 2022/4/11 10:16
 * @description
 */
@SpringBootTest
public class ActiviTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Autowired
    IdentityService identityService;

    @Autowired
    ManagementService managementService;

    @Autowired
    RunFlowableActinstDao runFlowableActinstDao;

    @Autowired
    HisFlowableActinstDao hisFlowableActinstDao;


    @Autowired
    ModelService modelService;

    @Autowired
    HistoryService historyService;

    @Autowired
    private ProcessDao processDao;


    public void deploy() throws FileNotFoundException {
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("leave.bpmn20.xml")
                //.addClasspathResource("OutPlan.Sequential.bpmn20.xml")
                //.addClasspathResource("????????????????????????.bpmn20.xml")
                .deploy();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
        logger.info("deplpyId:{}", deploy.getId());
        logger.info("processDefinitionId:{}", processDefinition.getId());
        logger.info("processDefinitionKey:{}", processDefinition.getKey());
    }


    public void start() {
        identityService.setAuthenticatedUserId("beck_guo");
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("initiator", "");
        varMap.put("skip", true);

        //List<String> assignList= new ArrayList<>();
        //assignList.add("001");
        //assignList.add("002");
        //varMap.put("assignList", assignList);

        //?????????????????????
        //varMap.put("director", "001");
        //varMap.put("manager", "002");
        //end

        varMap.put("_FLOWABLE_SKIP_EXPRESSION_ENABLED", true);
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("order",varMap);
        logger.info("businessKey:{}", instance.getBusinessKey());
        logger.info("id:{}", instance.getId());
        logger.info("definitionId:{}", instance.getProcessDefinitionId());
        logger.info("defintionKey:{}", instance.getProcessDefinitionKey());
    }


    public void myTask(String userId) {
        List<Task> tasks = taskService.createTaskQuery().taskCandidateOrAssigned(userId).list();
        if (null != tasks && tasks.size() > 0) {
            tasks.stream().forEach(s -> {
                logger.info("************************************");
                logger.info("taskId:{}", s.getId());
                logger.info("InstanceId:{}", s.getProcessInstanceId());
                logger.info("taskOwner:{}", s.getOwner());
                logger.info("taskAssignee:{}", s.getAssignee());
            });
        } else {
            logger.info("{},??????????????????", userId);
        }
    }

    public void allTask() {
        List<Task> tasks = taskService.createTaskQuery().list();
        if (null != tasks && tasks.size() > 0) {
            tasks.stream().forEach(s -> {
                logger.info("************************************");
                logger.info("parentTaskId:{}", s.getParentTaskId());
                logger.info("taskId:{}", s.getId());
                logger.info("ProcessInstanceId:{}", s.getProcessInstanceId());
                logger.info("taskOwner:{}", s.getOwner());
                logger.info("taskAssignee:{}", s.getAssignee());
                //logger.info("taskAssignee:{}", s.);
            });
        } else {
            logger.info("??????????????????");
        }
    }

    public void complete(String taskId) {
        taskService.complete(taskId);
    }

    public void backNodes(String processInstanceId) {
        List<ActivityInstance> userTask = runtimeService.createActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .activityType("userTask")
                .finished()
                .list();
        List<ActivityInstance> list = userTask.stream().filter(distinctByKey(ActivityInstance::getActivityId)).sorted(Comparator.comparing(ActivityInstance::getEndTime)).collect(Collectors.toList());
        for (int i = 0; i < list.size(); i++) {
            ActivityInstance activityInstance = list.get(i);
            logger.info("id:{}", activityInstance.getActivityId());
            logger.info("name:{}", activityInstance.getActivityName());
            logger.info("assign:{}", activityInstance.getAssignee());
        }

    }

    @Test
    public void back(String taskId, String distFlowElementId) {
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
            if ("?????????".equals(distActivity.getName())) {
                ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
                map.put("initiator", processInstance.getStartUserId());
            }
        }
        List<String> currentExecutionIds = new ArrayList<>();
        List<Execution> executions = runtimeService.createExecutionQuery().parentId(task.getProcessInstanceId()).list();
        for (Execution execution : executions) {
            currentExecutionIds.add(execution.getId());
        }
        deleteActivity(distFlowElementId,task.getProcessInstanceId());
        runtimeService.createChangeActivityStateBuilder()
                .processVariables(map)
                .moveExecutionsToSingleActivityId(currentExecutionIds, distFlowElementId)
                .changeState();

    }

    public void delegateTask(String taskId, String userId) {
        taskService.delegateTask(taskId, userId);
    }


    public void createSubTask(TaskEntityImpl ptask, String assignee) {
        ptask.setOwner(ptask.getAssignee());
        ptask.setAssignee(null);
        ptask.setCountEnabled(true);
        taskService.saveTask(ptask);

        TaskEntity task = (TaskEntity) taskService.newTask();
        task.setTenantId(ptask.getTenantId());
        task.setAssignee(assignee);
        task.setName(ptask.getName());
        task.setParentTaskId(ptask.getId());
        task.setProcessDefinitionId(ptask.getProcessDefinitionId());
        task.setProcessInstanceId(ptask.getProcessInstanceId());
        task.setTaskDefinitionKey(ptask.getTaskDefinitionKey());
        task.setTaskDefinitionId(ptask.getTaskDefinitionId());
        task.setFormKey(ptask.getFormKey());
        taskService.saveTask(task);
    }

    public TaskEntityImpl getTaskById(String taskId) {
        TaskEntityImpl task = (TaskEntityImpl) taskService.createTaskQuery().taskId(taskId).singleResult();
        return task;
    }

    public void completeAllTask() {
        List<Task> tasks = taskService.createTaskQuery().list();
        if (null != tasks && tasks.size() > 0) {
            tasks.stream().forEach(s -> {
                taskService.complete(s.getId());
            });
        } else {
            return;
        }
        completeAllTask();
    }

    public  void stopInstance(String processInstanceId) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (processInstance != null) {
            List<EndEvent> endNodes = null;
            BpmnModel bpmnModel = repositoryService.getBpmnModel((processInstance.getProcessDefinitionId()));
            if (bpmnModel != null) {
                Process process = bpmnModel.getMainProcess();
                endNodes = process.findFlowElementsOfType(EndEvent.class);
            }
            String endId = endNodes.get(0).getId();
            //3???????????????
            List<Execution> executions = runtimeService.createExecutionQuery().parentId(processInstanceId).list();
            List<String> executionIds = new ArrayList<>();
            executions.forEach(execution -> executionIds.add(execution.getId()));
            runtimeService.createChangeActivityStateBuilder()
                    .moveExecutionsToSingleActivityId(executionIds, endId)
                    .changeState();
        }
    }

    public void doStopInstance(){
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
        for(int i=0;i<list.size();i++){
            stopInstance(list.get(i).getId());
        }

    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    protected void deleteActivity(String disActivityId, String processInstanceId) {
        String tableName = managementService.getTableName(ActivityInstanceEntity.class);
        String sql = "select t.* from " + tableName + " t where t.PROC_INST_ID_=#{processInstanceId} and t.ACT_ID_ = #{disActivityId} " +
                " order by t.END_TIME_ ASC";
        List<ActivityInstance> disActivities = runtimeService.createNativeActivityInstanceQuery().sql(sql)
                .parameter("processInstanceId", processInstanceId)
                .parameter("disActivityId", disActivityId).list();
        //????????????????????????????????????
        if (!CollectionUtils.isEmpty(disActivities)) {
            ActivityInstance activityInstance = disActivities.get(0);
            sql = "select t.* from " + tableName + " t where t.PROC_INST_ID_=#{processInstanceId} and (t.END_TIME_ >= #{endTime} or t.END_TIME_ is null)";
            List<ActivityInstance> datas = runtimeService.createNativeActivityInstanceQuery().sql(sql).parameter("processInstanceId", processInstanceId)
                    .parameter("endTime", activityInstance.getEndTime()).list();
            List<String> runActivityIds = new ArrayList<>();
            if (!CollectionUtils.isEmpty(datas)) {
                datas.forEach(ai -> runActivityIds.add(ai.getId()));
                runFlowableActinstDao.deleteRunActinstsByIds(runActivityIds);
                hisFlowableActinstDao.deleteHisActinstsByIds(runActivityIds);
            }
        }
    }

    //???????????????????????????
    public void modeler(){
        List<AbstractModel> list = modelService.getModelsByModelType(0);
        list.forEach(s->{
            logger.info("modelerId:{}", s.getId());
            logger.info("ModelKey:{}", s.getKey());
            logger.info("modelerName:{}", s.getName());
            logger.info("ModelType:{}", s.getModelType());
        });

    }

    public void deployModler(){
        String modelId="8633904b-c440-11ec-bb14-025041000001";
        Model model = modelService.getModel(modelId);
        BpmnModel bpmnModel = modelService.getBpmnModel(model);
        Deployment deploy = repositoryService.createDeployment()
                .name(model.getName())
                .key(model.getKey())
                .category(model.getDescription())
                .addBpmnModel(model.getKey() + ".bpmn", bpmnModel)
                .deploy();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
        logger.info("deplpyId:{}", deploy.getId());
        logger.info("processDefinitionId:{}", processDefinition.getId());
        logger.info("processDefinitionKey:{}", processDefinition.getKey());
    }

    public void deploymoderAll(){
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        list.forEach(s->{
            logger.info("deployId:{}",s.getId());
            logger.info("deployName:{}",s.getName());
            logger.info("deployTenantId:{}",s.getTenantId());
        });

    }

    public void delDeploymoder(){

        repositoryService.deleteDeployment("876296eb-c3a8-11ec-8738-025041000001");
    }

    public void getStartUser(){
        String startUserId = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId("397946b9-c436-11ec-a739-025041000001")
                .singleResult()
                .getStartUserId();
        logger.info("startUserId:{}",startUserId);
    }

    public void noFishProcess(){
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId("f7dc5445-e7c8-11ec-ae22-025041000001")
                .list();
        list.forEach(s->{
            logger.info("????????????:{}",s.getProcessDefinitionName());
            logger.info("??????id:{}",s.getId());
            logger.info("?????????:{}",s.getStartUserId());

            List<ActivityInstance> tasks = runtimeService.createActivityInstanceQuery()
                    .processInstanceId(s.getId())
                    .unfinished()
                    .activityType("userTask")
                    .list();
            tasks.forEach(k->{
                logger.info("ActivityName:{}",k.getActivityName());
                logger.info("taskId:{}",k.getTaskId());
                if(StringUtils.isBlank(k.getAssignee())){
                    List<RolePO> users = managementService.executeCommand(new ApplyUserCmd(k.getTaskId()));
                    logger.info("??????????????????{}", JSON.toJSONString(users));
                }else{
                    logger.info("??????????????????{}", k.getAssignee());
                }
            });
        });
    }

    public void lastApplyUser(){
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId("f7dc5445-e7c8-11ec-ae22-025041000001")
                .finished()
                .orderByHistoricTaskInstanceEndTime()
                .desc()
                .list();
        if(!StringUtils.isBlank(list.get(0).getAssignee())){
            logger.info("???????????????????????????{}",list.get(0).getAssignee());
        }else{
            List<RolePO> users = managementService.executeCommand(new ApplyUserCmd(list.get(0).getId()));
            logger.info("????????????????????????{}", JSON.toJSONString(users));
        }

    }

    public void getHisComplete(){
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .finished()
                .list();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(o->{
                    logger.info("name:{}",o.getName());
                    logger.info("assignee:{}",o.getAssignee());
                    logger.info("createTime:{}",o.getCreateTime());
                    logger.info("endTime:{}",o.getEndTime());
                    logger.info("duration:{}",o.getDurationInMillis());
                });
    }

    public void myCreateProcess(){
        List<HistoricProcessInstance> list = historyService
                .createHistoricProcessInstanceQuery()
                .startedBy("")
                .list();

    }

    //??????
    public void taskDelegated(String taskId){
        taskService.setAssignee(taskId,"003");
    }

    public void tasking(){
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
    }

    public void status(String processId){
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processId)
                .singleResult();
        logger.info("isSuspended:{}",instance.isSuspended());
    }

    public void nodes(){
        String  processId="871b2ac9-d751-11ec-9849-025041000001";
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
        BpmnModel model = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        List<Process> processes = model.getProcesses();
        processes.forEach(o->{
            Collection<FlowElement> flowElements = o.getFlowElements();
            flowElements.forEach(k->{
                if(k instanceof UserTask){
                    logger.info("ExtensionId:{}",k.getId());
                    logger.info("taskName:{}",k.getName());
                    logger.info("col:{}",k.getXmlColumnNumber());
                    logger.info("row:{}",k.getXmlRowNumber());
                }
            });
        });
    }

    public void currentNode(){
        List<Map> list = processDao.applyNodes("f7dc5445-e7c8-11ec-ae22-025041000001");
        List<Map<String, Object>> tasks = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(o -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("nodeName", o.get("nodeName"));
                    if (null!=o.get("assignee")) {
                        List<RolePO> roles = managementService.executeCommand(new ApplyUserCmd(o.get("id").toString()));
                        map.put("assign", JSON.toJSONString(roles));
                    } else {
                        map.put("assign",o.get("assignee"));
                    }
                    tasks.add(map);
                });
    }

    @Test
    public void test() throws Exception{
        //deploy();
        //allTask();
        //start();
        //delegateTask("70497333-bbd6-11ec-b458-025041000001","beck_guo");
        //taskService.resolveTask("0ffd314c-bb03-11ec-9af8-025041000001");
        //complete("bbf70053-bbc9-11ec-93e1-025041000001");
        //TaskEntityImpl task = getTaskById("47a85908-bc60-11ec-a156-025041000001");
        //createSubTask(task, "002");
        //completeAllTask();
         //doStopInstance();
        //taskService.resolveTask("378634f9-bc5f-11ec-9131-025041000001");
        //taskService.complete("7ada8fae-c542-11ec-b6f5-025041000001");
        //myTask("001");
        //myTask("002");
        //backNodes("ecc3347f-c20a-11ec-a764-025041000001");
        //back("3cf376a1-c20b-11ec-baea-025041000001","sid-5D1F91EB-4019-48ED-B0E7-AE948451C539");
        //allTask();
        //modeler();
        //deployModler();
        //deploymoderAll();
        //delDeploymoder();
        //getStartUser();
        //lastApplyUser();
        //noFishProcess();
        //getHisComplete();
        //taskDelegated("fa497283-d5bf-11ec-a71b-025041000001");
        //status("871b2ac9-d751-11ec-9849-025041000001");
        //nodes();
        currentNode();
    }


}
