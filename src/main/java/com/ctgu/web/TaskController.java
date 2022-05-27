package com.ctgu.web;

import com.ctgu.model.BO.ResultMsgBO;
import com.ctgu.model.BO.TaskHandleBO;
import com.ctgu.model.types.TaskHandleEnum;
import com.ctgu.service.IProcessService;
import com.ctgu.service.ITaskService;
import com.ctgu.service.TaskBaseHandle;
import com.ctgu.util.ApplicationContextUtils;
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

    @Autowired
    ITaskService iTaskService;

    @Autowired
    IProcessService iProcessService;

    @GetMapping("getMyTask/{username}")
    public ResultMsgBO getMyTask(@PathVariable String username){
        return iTaskService.getMyTask(username);
    }

    @GetMapping("getBackNodes/{processInstanceId}")
    public ResultMsgBO getBackNodes(@PathVariable String processInstanceId){
        return iProcessService.getBackNodes(processInstanceId);
    }

    @GetMapping("getHisTask/{username}")
    public ResultMsgBO getHisTask(@PathVariable String username){
        return iTaskService.getHisTask(username);
    }


    @GetMapping("getTaskIng")
    public ResultMsgBO taskIng(){
        return iTaskService.taskIng();
    }


    @GetMapping("getTaskAllNode/{processId}")
    public ResultMsgBO getTaskAllNode(@PathVariable String processId){
        return iTaskService.getTaskAllNode(processId);
    }


    @PostMapping("handle")
    public ResultMsgBO handle(@RequestBody TaskHandleBO model){
        String type = TaskHandleEnum.getServiceByType(model.getType());
        TaskBaseHandle baseHandle=ApplicationContextUtils.popBean(type);
        baseHandle.execute(model);
        return new ResultMsgBO(0,"ok",null);
    }

    @GetMapping("getTaskEnd")
    public ResultMsgBO getTaskEnd(){
        return iTaskService.taskEnd();
    }
}
