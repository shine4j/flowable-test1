package com.ctgu.web;

import com.alibaba.fastjson.JSON;
import com.ctgu.BO.PageQueryBO;
import com.ctgu.BO.ResultMsgBO;
import com.ctgu.PO.RolePO;
import com.ctgu.service.IProcessService;
import com.ctgu.service.ITaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author beck_guo
 * @create 2022/4/29 13:55
 * @description
 */
@RestController
@RequestMapping("api/task")
public class TaskController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ITaskService iTaskService;

    @Autowired
    IProcessService iProcessService;

    @GetMapping("getTask")
    public ResultMsgBO getTask(RolePO role, PageQueryBO queryBO){
        logger.info("role:{},pageQuery:{}", JSON.toJSONString(role),JSON.toJSONString(queryBO));
        return iTaskService.getTask();
    }

    @GetMapping("getBackNodes/{processInstanceId}")
    public ResultMsgBO getBackNodes(@PathVariable String processInstanceId){
        return iProcessService.getBackNodes(processInstanceId);
    }

    @GetMapping("doBack/{taskId}/{distFlowElementId}")
    public ResultMsgBO doBack(@PathVariable String taskId,@PathVariable String distFlowElementId){
        return iTaskService.doBack(taskId,distFlowElementId);
    }

    @GetMapping("getHisTask")
    public ResultMsgBO getHisTask(){
        return iTaskService.getHisTask();
    }

    @GetMapping("doComplete/{taskId}")
    public ResultMsgBO doComplete(@PathVariable String taskId){
        return iTaskService.doComplete(taskId);
    }

    @GetMapping("doCommunicate/{taskId}/{assignee}")
    public ResultMsgBO doCommunicate(@PathVariable String taskId,@PathVariable String assignee){
        return iTaskService.doCommunicate(taskId,assignee);
    }
}
