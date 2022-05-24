package com.ctgu.service;

import com.ctgu.model.BO.TaskHandleBO;

/**
 * @Author beck_guo
 * @create 2022/5/23 14:29
 * @description  审批处理公共方法
 */
public abstract class TaskBaseHandle {

    public void execute(TaskHandleBO model){
        handle(model);
    }

    public abstract void handle(TaskHandleBO model);
}
