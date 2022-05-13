package com.ctgu.web;

import com.alibaba.fastjson.JSON;
import com.ctgu.BO.PageQueryBO;
import com.ctgu.BO.ResultMsgBO;
import com.ctgu.BO.TaskHandleBO;
import com.ctgu.PO.RolePO;
import com.ctgu.service.IProcessService;
import com.ctgu.service.ITaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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

    @GetMapping("getTask/{username}")
    public ResultMsgBO getTask(@PathVariable String username){
        return iTaskService.getTask(username);
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

    @PostMapping("doComplete")
    public ResultMsgBO doComplete(@RequestBody TaskHandleBO model){
        return iTaskService.doComplete(model);
    }

    @PostMapping("addSign")
    public ResultMsgBO addSign(@RequestBody TaskHandleBO model){
        return iTaskService.addSign(model);
    }
}
