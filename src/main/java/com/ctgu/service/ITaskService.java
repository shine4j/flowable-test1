package com.ctgu.service;

import com.ctgu.model.BO.pager.PageQueryBO;
import com.ctgu.model.BO.ResultMsgBO;
import com.ctgu.model.BO.TaskQueryBO;
import com.ctgu.model.BO.pager.PagerModel;
import com.ctgu.model.VO.TaskVo;
import com.github.pagehelper.Page;

import java.util.Map;

/**
 * @Author beck_guo
 * @create 2022/4/29 15:16
 * @description
 */
public interface ITaskService {

    /**
     * 查询我的任务
     */
    PagerModel<TaskVo> getMyTask(TaskQueryBO params, PageQueryBO query);

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


    /**
     * 得到流程实例的所有回退节点
     */
    ResultMsgBO getBackNodes(String processInstanceId);


}
