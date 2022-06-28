package com.ctgu.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.engine.IdentityService;
import org.flowable.engine.TaskService;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.idm.api.User;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author beck_guo
 * @create 2022/6/28 10:49
 * @description
 */
@Component
public class ProcessUtils {

    Logger logger = LoggerFactory.getLogger(getClass());

    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private TaskService taskService;

    @Autowired
    private IdentityService identityService;

    public List<Map> getApprovers(String processInstanceId) {
        List<Map> mps=new ArrayList<>();
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if (CollectionUtils.isNotEmpty(list)) {
            list.stream().filter(s->s.getOwner()==null||s.getAssignee()!=null).forEach(task -> {
                List<String> newUser=new ArrayList<>();
                if (StringUtils.isNotBlank(task.getAssignee())) {
                    //1.审批人ASSIGNEE_是用户id
                    User user = identityService.createUserQuery().userId(task.getAssignee()).singleResult();
                    if (user != null) {
                        String username=user.getLastName()+user.getFirstName();
                        newUser.add(username);
                    }
                    //2.审批人ASSIGNEE_是组id
                    List<User> gusers = identityService.createUserQuery().memberOfGroup(task.getAssignee()).list();
                    if (CollectionUtils.isNotEmpty(gusers)) {
                        gusers.forEach(o->{
                            String username=user.getLastName()+user.getFirstName();
                            newUser.add(username);
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
                                    String username=user.getLastName()+user.getFirstName();
                                    newUser.add(username);
                                }
                            } else {
                                //4.审批人ASSIGNEE_为空,组id
                                List<User> gusers = identityService.createUserQuery().memberOfGroup(identityLink.getGroupId()).list();
                                if (CollectionUtils.isNotEmpty(gusers)) {
                                    gusers.forEach(o->{
                                        String username=o.getLastName()+o.getFirstName();
                                        newUser.add(username);
                                    });
                                }
                            }
                        });
                    }
                }
                Map mp=new HashMap();
                mp.put("startTime",sdf.format(task.getCreateTime()));
                mp.put("nodeName",task.getName());
                mp.put("user", newUser);
                mps.add(mp);
            });
        }
        return mps;
    }
}
