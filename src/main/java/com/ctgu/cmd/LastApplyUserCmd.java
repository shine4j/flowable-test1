package com.ctgu.cmd;

import com.ctgu.PO.RolePO;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.identitylink.service.impl.persistence.entity.HistoricIdentityLinkEntity;
import org.flowable.identitylink.service.impl.persistence.entity.IdentityLinkEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author beck_guo
 * @create 2022/4/26 11:38
 * @description
 */

public class LastApplyUserCmd implements Command<List<RolePO>> {


    Logger logger = LoggerFactory.getLogger(getClass());

    private String taskId;

    public LastApplyUserCmd(String taskId){
        this.taskId=taskId;
    }

    @Override
    public List<RolePO> execute(CommandContext commandContext) {

        List<RolePO> list=new ArrayList<>();
        List<HistoricIdentityLinkEntity> ids = CommandContextUtil.getHistoricIdentityLinkService()
                .findHistoricIdentityLinksByTaskId(taskId);
        ids.forEach(k->{
            RolePO rolePO=new RolePO();
            rolePO.setName(k.getGroupId());
            list.add(rolePO);
        });
        return list;
    }
}
