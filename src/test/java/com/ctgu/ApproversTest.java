package com.ctgu;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.engine.IdentityService;
import org.flowable.engine.TaskService;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.idm.api.User;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author beck_guo
 * @create 2022/6/23 11:32
 * @description
 */
@SpringBootTest
public class ApproversTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private TaskService taskService;

    @Autowired
    private IdentityService identityService;

    public List<Map> getApprovers(String processInstanceId) {
        List<Map> mps=new ArrayList<>();
        List<Map> users = new ArrayList<>();
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if (CollectionUtils.isNotEmpty(list)) {
            list.stream().filter(s->s.getOwner()==null).forEach(task -> {
                Map newUser=new HashMap();
                newUser.put("startTime",sdf.format(task.getCreateTime()));
                if (StringUtils.isNotBlank(task.getAssignee())) {
                    //1.审批人ASSIGNEE_是用户id
                    User user = identityService.createUserQuery().userId(task.getAssignee()).singleResult();
                    if (user != null) {
                        newUser.put("username",user.getLastName()+user.getFirstName());
                        users.add(newUser);
                    }
                    //2.审批人ASSIGNEE_是组id
                    List<User> gusers = identityService.createUserQuery().memberOfGroup(task.getAssignee()).list();
                    if (CollectionUtils.isNotEmpty(gusers)) {
                        gusers.forEach(o->{
                            newUser.put("username",o.getLastName()+o.getFirstName());
                            users.add(newUser);
                        });
                    }
                } else {
                    List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());
                    if (CollectionUtils.isNotEmpty(identityLinks)) {
                        identityLinks.forEach(identityLink -> {
                            //3.审批人ASSIGNEE_为空,用户id
                            if (StringUtils.isNotBlank(identityLink.getUserId())) {
                                User user = identityService.createUserQuery().userId(identityLink.getUserId()).singleResult();
                                if (user != null) {
                                    newUser.put("username",user.getLastName()+user.getFirstName());
                                    users.add(newUser);
                                }
                            } else {
                                //4.审批人ASSIGNEE_为空,组id
                                List<User> gusers = identityService.createUserQuery().memberOfGroup(identityLink.getGroupId()).list();
                                if (CollectionUtils.isNotEmpty(gusers)) {
                                    gusers.forEach(o->{
                                        newUser.put("username",o.getLastName()+o.getFirstName());
                                        users.add(newUser);
                                    });
                                }
                            }
                        });
                    }
                }
                Map mp=new HashMap();
                mp.put("节点名称",task.getName());
                mp.put("审批人",users);
                mps.add(mp);
            });
        }
        return mps;
    }

    @Test
    public void t(){
        List<Map> users = getApprovers("4061f7be-f2b4-11ec-9239-025041000001");
        logger.info("users:{}", JSON.toJSONString(users));
    }
}
