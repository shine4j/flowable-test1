package com.ctgu.service;

import com.ctgu.BO.ResultMsgBO;
import com.ctgu.BO.TaskHandleBO;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;

/**
 * @Author beck_guo
 * @create 2022/4/29 15:16
 * @description
 */
public interface ITaskService {

    /**
     * 查询所有任务
     */
    ResultMsgBO getTask(String username);

    /**
     * 通过任务
     */
    ResultMsgBO doComplete(TaskHandleBO model);

    /**
     * 回退任务
     */
    ResultMsgBO doBack(String taskId, String distFlowElementId);

    /**
     * 委派任务
     */
    ResultMsgBO doDelegateTask(String taskId, String userId);

    /**
     * 已审批任务
     */
    ResultMsgBO getHisTask();

    /**
     *沟通
     */
    ResultMsgBO addSign(TaskHandleBO model);


}
