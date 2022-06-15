package com.ctgu;

import com.alibaba.fastjson.JSON;
import com.ctgu.model.types.TaskHandleEnum;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author beck_guo
 * @create 2022/5/23 11:18
 * @description
 */
@SpringBootTest
public class OtherTest {

    Logger logger= LoggerFactory.getLogger(getClass());

    @Autowired
    private RepositoryService repositoryService;

    public void t1(){
        logger.info("==={}", TaskHandleEnum.getServiceByType("COMPLETE"));
    }

    public void define(){
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        List<Map<String,Object>> defines = new ArrayList<>();
        list.forEach(s->{
            Map<String,Object> map =  new HashMap<>();
            map.put("deployId",s.getId());
            map.put("deployName",s.getName());
            map.put("deployKey",s.getKey());
            defines.add(map);
        });
        logger.info("defines:{}", JSON.toJSONString(defines));
    }

    @Test
    public void test(){
        define();
    }
}
