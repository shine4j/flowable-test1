package com.ctgu.service;

import com.ctgu.BO.ResultMsgBO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author beck_guo
 * @create 2022/4/29 15:31
 * @description
 */
public interface IProcessService {

    /**
     * 得到实例的流程图
     */
    void getImage(String processInstanceId, HttpServletResponse response) throws IOException;

    /**
     * 得到流程实例的所有回退节点
     */
    ResultMsgBO getBackNodes(String processInstanceId);

    /**
     * 终止流程实例
     */
    ResultMsgBO doStopProcess(String processInstanceId);


    /**
     *未完成实例
     */
    ResultMsgBO getNoFish();

}
