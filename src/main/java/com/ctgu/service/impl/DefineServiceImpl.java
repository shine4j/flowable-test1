package com.ctgu.service.impl;

import com.ctgu.model.BO.ResultMsgBO;
import com.ctgu.service.IDefineService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class DefineServiceImpl implements IDefineService {

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    protected RuntimeService runtimeService;

    @Override
    public ResultMsgBO getAllDefine() {
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        List<Map<String,Object>> defines = new ArrayList<>();
        list.forEach(s->{
            Map<String,Object> map =  new HashMap<>();
            map.put("deployId",s.getId());
            map.put("deployName",s.getName());
            map.put("deployKey",s.getKey());
            defines.add(map);
        });
        return new ResultMsgBO(0,"ok",defines);
    }


    @Override
    public ResultMsgBO startByKey(String key,String username) {
        identityService.setAuthenticatedUserId(username);
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("initiator", "");
        varMap.put("skip", true);
        varMap.put("_FLOWABLE_SKIP_EXPRESSION_ENABLED", true);
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(key, varMap);
        return new ResultMsgBO(0, "ok", null);
    }
}
