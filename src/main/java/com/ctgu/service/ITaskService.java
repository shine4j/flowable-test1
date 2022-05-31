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
    PagerModel<Map> getMyTask(TaskQueryBO params, PageQueryBO query);

    /**
     * 查询我已审批任务
     */
    PagerModel<Map> getMyHisTask(TaskQueryBO params, PageQueryBO query);


    /**
     * 我提交的实例
     */
    PagerModel<Map> getMyStart(TaskQueryBO params,PageQueryBO query);


    /**
     *正在运行的任务
     */
    PagerModel<Map> taskIng(TaskQueryBO params,PageQueryBO query);


    /**
     *已经完成的任务
     */
    PagerModel<Map> taskEnd(TaskQueryBO params,PageQueryBO query);


    /**
     *得到任务所有节点
     */
    ResultMsgBO getTaskAllNode(String processId);


    /**
     * 得到流程实例的所有回退节点
     */
    ResultMsgBO getBackNodes(String processInstanceId);


}
