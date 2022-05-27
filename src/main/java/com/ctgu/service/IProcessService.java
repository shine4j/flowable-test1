package com.ctgu.service;

import com.ctgu.model.BO.ResultMsgBO;

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
     *运行中的实例
     */
    ResultMsgBO getNoFish();

    /**
     *已完成的实例
     */
    ResultMsgBO getFish();


    /**
     *启动流程
     */
    ResultMsgBO startByKey(String key,String username);

    /**
     *实例的审批记录
     */
    ResultMsgBO applyNodes(String processInstanceId);


}
