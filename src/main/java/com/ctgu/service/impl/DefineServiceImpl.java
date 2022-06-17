package com.ctgu.service.impl;

import com.alibaba.fastjson.JSON;
import com.ctgu.cmd.AddCommentCmd;
import com.ctgu.dao.BaseBusinessDao;
import com.ctgu.model.BO.AddCommentBO;
import com.ctgu.model.BO.AddFlowBO;
import com.ctgu.model.BO.ResultMsgBO;
import com.ctgu.model.PO.BaseBusinessPO;
import com.ctgu.service.IDefineService;
import com.ctgu.util.ApplicationContextUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.flowable.engine.IdentityService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author beck_guo
 * @create 2022/5/5 17:29
 * @description
 */
@Service
@Transactional
public class DefineServiceImpl implements IDefineService {

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    ManagementService managementService;

    @Override
    public ResultMsgBO getAllDefine() {
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        List<Map<String, Object>> defines = new ArrayList<>();
        list.forEach(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("deployId", s.getId());
            map.put("deployName", s.getName());
            map.put("deployKey", s.getKey());
            defines.add(map);
        });
        return new ResultMsgBO(0, "ok", defines);
    }


    @SneakyThrows
    @Override
    public ResultMsgBO startByKey(AddFlowBO model) throws Exception{
        identityService.setAuthenticatedUserId(model.getUsername());
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("initiator", "");
        varMap.put("skip", true);
        varMap.put("_FLOWABLE_SKIP_EXPRESSION_ENABLED", true);
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(model.getKey(), varMap);
        runtimeService.setProcessInstanceName(instance.getId(), model.getSubject());
        AddCommentBO comment = new AddCommentBO();
        comment.setProcessId(instance.getId());
        comment.setUserId(model.getUsername());
        comment.setMessage(model.getUsername() + "提交流程");
        managementService.executeCommand(new AddCommentCmd(comment));
        if (null != model.getFlow()) {
            String bean = model.getKey() + "Dao";
            BaseBusinessDao businessDao = ApplicationContextUtils.popBean(bean);
            String className = model.getKey().substring(0, 1).toUpperCase() + model.getKey().substring(1);
            String fullClassName="com.ctgu.model.PO.flow."+className+"PO";
            Class forName = Class.forName(fullClassName);
            Object instance1 = forName.newInstance();
            ObjectMapper mapper = new ObjectMapper();
            BaseBusinessPO flowPo = (BaseBusinessPO) mapper.readValue(JSON.toJSONString(model.getFlow()),instance1.getClass());
            businessDao.save(flowPo);
        }
        return new ResultMsgBO(0, "ok", null);
    }
}
