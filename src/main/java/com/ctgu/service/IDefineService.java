package com.ctgu.service;

import com.ctgu.model.BO.AddFlowBO;
import com.ctgu.model.BO.ResultMsgBO;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @Author beck_guo
 * @create 2022/5/5 17:28
 * @description
 */
public interface IDefineService {

    ResultMsgBO getAllDefine();

    /**
     *启动流程
     */
    ResultMsgBO startByKey(AddFlowBO model) throws ClassNotFoundException, JsonProcessingException, Exception;
}
