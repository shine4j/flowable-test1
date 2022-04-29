package com.ctgu.service;

import com.ctgu.BO.ResultMsgBO;

/**
 * @Author beck_guo
 * @create 2022/4/29 15:16
 * @description
 */
public interface ITaskService {

    ResultMsgBO getTask();

    ResultMsgBO getBackNodes(String processInstanceId);

    ResultMsgBO doComplete(String taskId);

    ResultMsgBO  doBack(String taskId, String distFlowElementId);
}
