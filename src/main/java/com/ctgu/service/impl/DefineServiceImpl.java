package com.ctgu.service.impl;

import com.ctgu.BO.ResultMsgBO;
import com.ctgu.service.IDefineService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
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
}
