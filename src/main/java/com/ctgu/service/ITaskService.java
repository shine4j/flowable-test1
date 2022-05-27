package com.ctgu.service;

import com.ctgu.model.BO.ResultMsgBO;
import com.ctgu.model.BO.TaskHandleBO;

/**
 * @Author beck_guo
 * @create 2022/4/29 15:16
 * @description
 */
public interface ITaskService {

    /**
     * 查询我的任务
     */
    ResultMsgBO getMyTask(String username);

    /**
     * 查询我已审批任务
     */
    ResultMsgBO getHisTask(String username);


    /**
     * 我提交的实例
     */
    ResultMsgBO getMyStart(String username);


    /**
     *正在运行的任务
     */
    ResultMsgBO taskIng();


    /**
     *已经完成的任务
     */
    ResultMsgBO taskEnd();


    /**
     *得到任务所有节点
     */
    ResultMsgBO getTaskAllNode(String processId);


}
