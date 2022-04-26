package com.ctgu.cmd;

import com.ctgu.PO.RolePO;
import org.checkerframework.checker.units.qual.A;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.ActivityInstanceQuery;
import org.flowable.engine.task.Comment;
import org.flowable.identitylink.service.impl.persistence.entity.IdentityLinkEntity;
import org.flowable.task.api.Task;
import org.junit.platform.commons.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Author beck_guo
 * @create 2022/4/26 11:38
 * @description
 */

public class CurrenApplyUserCmd implements Command<List<RolePO>> {


    Logger logger = LoggerFactory.getLogger(getClass());

    private String taskId;

    public CurrenApplyUserCmd(String taskId){
        this.taskId=taskId;
    }

    @Override
    public List<RolePO> execute(CommandContext commandContext) {

        List<RolePO> list=new ArrayList<>();
        List<IdentityLinkEntity> ids = CommandContextUtil.getIdentityLinkService(commandContext).findIdentityLinksByTaskId(taskId);
        ids.forEach(k->{
            RolePO rolePO=new RolePO();
            rolePO.setName(k.getGroupId());
            list.add(rolePO);
        });
        return list;
    }
}
