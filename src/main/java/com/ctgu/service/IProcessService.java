package com.ctgu.service;

import com.ctgu.model.BO.ProcessQueryBO;
import com.ctgu.model.BO.ResultMsgBO;
import com.ctgu.model.BO.TaskQueryBO;
import com.ctgu.model.BO.pager.PageQueryBO;
import com.ctgu.model.BO.pager.PagerModel;
import org.flowable.engine.task.Comment;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
     *运行中的实例
     */
    PagerModel<Map> getNoFish(ProcessQueryBO params, PageQueryBO query);

    /**
     *已完成的实例
     */
    PagerModel<Map> getFish(ProcessQueryBO params, PageQueryBO query);


    /**
     *实例的审批记录
     */
    ResultMsgBO applyNodes(String processInstanceId);

    /**
     *实例的审批记录
     */
    List<Map> getComment(String processInstanceId);
}
