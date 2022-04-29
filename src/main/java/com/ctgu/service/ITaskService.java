package com.ctgu.service;

import com.ctgu.BO.ResultMsgBO;

/**
 * @Author beck_guo
 * @create 2022/4/29 15:16
 * @description
 */
public interface ITaskService {

    /**
     * 查询所有任务
     * @return
     */
    ResultMsgBO getTask();

    /**
     * 通过任务
     * @param taskId
     * @return
     */
    ResultMsgBO doComplete(String taskId);

    /**
     * 回退任务
     * @param taskId
     * @param distFlowElementId
     * @return
     */
    ResultMsgBO doBack(String taskId, String distFlowElementId);

    /**
     * 委派任务
     * @param taskId
     * @param userId
     * @return
     */
    ResultMsgBO doDelegateTask(String taskId, String userId);
}
