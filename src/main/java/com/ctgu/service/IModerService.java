package com.ctgu.service;

import com.ctgu.model.BO.ResultMsgBO;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author beck_guo
 * @create 2022/4/29 15:38
 * @description
 */
public interface IModerService {

    /**
     * 得到流程定义图片
     * @param modeId
     * @param resp
     * @throws IOException
     */
    void getImageById(String modeId, HttpServletResponse resp) throws IOException;

    /**
     * 得到流程定义xml
     * @param modeId
     * @param resp
     */
    void getXmlById(String modeId, HttpServletResponse resp);

    /**
     * 得到所有流程的定义
     * @return
     */
    ResultMsgBO getAllModer();

    /**
     * 部署流程定义
     * @param moderId
     * @return
     */
    ResultMsgBO  deployModerById(String moderId);

}
