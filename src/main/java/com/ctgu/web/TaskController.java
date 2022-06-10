package com.ctgu.web;

import com.ctgu.cmd.AddCommentCmd;
import com.ctgu.model.BO.ResultMsgBO;
import com.ctgu.model.BO.TaskHandleBO;
import com.ctgu.model.BO.TaskQueryBO;
import com.ctgu.model.BO.pager.PageQueryBO;
import com.ctgu.model.BO.pager.PagerModel;
import com.ctgu.model.types.TaskHandleEnum;
import com.ctgu.service.IProcessService;
import com.ctgu.service.ITaskService;
import com.ctgu.service.TaskBaseHandle;
import com.ctgu.util.ApplicationContextUtils;
import org.flowable.engine.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


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

    @Autowired
    ManagementService managementService;

    @GetMapping("getMyTask")
    public ResultMsgBO getMyTask(TaskQueryBO params, PageQueryBO query){
        PagerModel<Map> myTask = iTaskService.getMyTask(params, query);
        return new ResultMsgBO(0,"ok",myTask);

    }

    @GetMapping("getBackNodes/{processInstanceId}")
    public ResultMsgBO getBackNodes(@PathVariable String processInstanceId){
        return iTaskService.getBackNodes(processInstanceId);
    }

    @GetMapping("getHisTask")
    public ResultMsgBO getMyHisTask(TaskQueryBO params, PageQueryBO query){
        PagerModel<Map> myTask = iTaskService.getMyHisTask(params, query);
        return new ResultMsgBO(0,"ok",myTask);
    }

    @GetMapping(value = "getMyStart")
    public ResultMsgBO getMyStart(TaskQueryBO parms,PageQueryBO query) {
        PagerModel<Map> myStart = iTaskService.getMyStart(parms, query);
        return  new ResultMsgBO(0,"ok",myStart);
    }


    @GetMapping("getTaskIng")
    public ResultMsgBO taskIng(TaskQueryBO parms,PageQueryBO query){
        PagerModel<Map> taskIng = iTaskService.taskIng(parms, query);
        return  new ResultMsgBO(0,"ok",taskIng);
    }

    @GetMapping("getTaskEnd")
    public ResultMsgBO getTaskEnd(TaskQueryBO parms,PageQueryBO query){
        PagerModel<Map> taskEnd = iTaskService.taskEnd(parms, query);
        return  new ResultMsgBO(0,"ok",taskEnd);
    }


    @GetMapping("getTaskAllNode/{processId}")
    public ResultMsgBO getTaskAllNode(@PathVariable String processId){
        return iTaskService.getTaskAllNode(processId);
    }


    @PostMapping("handle")
    public ResultMsgBO handle(@RequestBody TaskHandleBO model){
        String type = TaskHandleEnum.getServiceByType(model.getType());
        TaskBaseHandle baseHandle=ApplicationContextUtils.popBean(type);
        baseHandle.setManagementService(managementService);
        baseHandle.execute(model);
        return new ResultMsgBO(0,"ok",null);
    }


}
