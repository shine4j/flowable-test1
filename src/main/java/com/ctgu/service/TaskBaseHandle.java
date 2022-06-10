package com.ctgu.service;

import com.ctgu.cmd.AddCommentCmd;
import com.ctgu.model.BO.TaskHandleBO;
import org.flowable.engine.ManagementService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author beck_guo
 * @create 2022/5/23 14:29
 * @description  审批处理公共方法
 */
@Transactional
public abstract class TaskBaseHandle {

    ManagementService managementService;

    public void setManagementService(ManagementService managementService){
        this.managementService=managementService;
    }

    public void execute(TaskHandleBO model){
        handle(model);
        if(null!=model.getComment()&&model.getComment().getMessage().length()>0){
            managementService.executeCommand(new AddCommentCmd(model.getComment()));
        }
    }

    public abstract void handle(TaskHandleBO model);
}
