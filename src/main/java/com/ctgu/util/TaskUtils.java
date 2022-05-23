package com.ctgu.util;

import com.ctgu.model.BO.TaskHandleBO;
import com.ctgu.dao.HisFlowableActinstDao;
import com.ctgu.dao.RunFlowableActinstDao;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.persistence.entity.ActivityInstanceEntity;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @Author beck_guo
 * @create 2022/4/29 15:50
 * @description
 */
@Component
public class TaskUtils {

    @Autowired
    RunFlowableActinstDao runFlowableActinstDao;

    @Autowired
    HisFlowableActinstDao hisFlowableActinstDao;

    @Autowired
    ManagementService managementService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;


    public  <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public void deleteActivity(String disActivityId, String processInstanceId) {
        String tableName = managementService.getTableName(ActivityInstanceEntity.class);
        String sql = "select t.* from " + tableName + " t where t.PROC_INST_ID_=#{processInstanceId} and t.ACT_ID_ = #{disActivityId} " +
                " order by t.END_TIME_ ASC";
        List<ActivityInstance> disActivities = runtimeService.createNativeActivityInstanceQuery().sql(sql)
                .parameter("processInstanceId", processInstanceId)
                .parameter("disActivityId", disActivityId).list();
        //删除运行时和历史节点信息
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

    public void creatSubTask(TaskHandleBO model) {
        TaskEntityImpl task = (TaskEntityImpl)taskService.createTaskQuery()
                .taskId(model.getTaskId()).singleResult();
        task.setOwner(task.getAssignee());
        task.setAssignee(null);
        task.setCountEnabled(true);
        task.setScopeType(model.getType());
        taskService.saveTask(task);
        Optional.ofNullable(model.getUsers()).orElse(new ArrayList<>())
                .forEach(o->{
                    TaskEntity newTask = (TaskEntity) taskService.newTask();
                    newTask.setTenantId(task.getTenantId());
                    newTask.setAssignee(o);
                    newTask.setName(task.getName());
                    newTask.setParentTaskId(task.getId());
                    newTask.setProcessDefinitionId(task.getProcessDefinitionId());
                    newTask.setProcessInstanceId(task.getProcessInstanceId());
                    newTask.setTaskDefinitionKey(task.getTaskDefinitionKey());
                    newTask.setTaskDefinitionId(task.getTaskDefinitionId());
                    newTask.setFormKey(task.getFormKey());
                    newTask.setCreateTime(new Date());
                    taskService.saveTask(newTask);
                });
    }
}
