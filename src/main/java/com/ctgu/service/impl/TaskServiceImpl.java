package com.ctgu.service.impl;

import com.ctgu.BO.ResultMsgBO;
import com.ctgu.service.ITaskService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author beck_guo
 * @create 2022/4/29 15:17
 * @description
 */
@Service
public class TaskServiceImpl implements ITaskService {

    @Autowired
    private TaskService taskService;

    @Override
    public ResultMsgBO getTask() {
        List<Task> list = taskService.createTaskQuery().list();
        List<Map<String,Object>> tasks = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(o->{
                    Map<String,Object> map =  new HashMap<>();
                    map.put("id",o.getId());
                    map.put("name",o.getName());
                    map.put("createTime",o.getCreateTime());
                    map.put("formKey",o.getFormKey());
                    tasks.add(map);
                });
        return new ResultMsgBO(0,"ok",tasks);
    }
}
