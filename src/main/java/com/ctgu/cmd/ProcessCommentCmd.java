package com.ctgu.cmd;

import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.persistence.entity.CommentEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.task.Comment;

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @Author beck_guo
 * @create 2022/4/26 11:38
 * @description
 */

public class ProcessCommentCmd implements Command<List<Map>> {

    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String processId;

    public ProcessCommentCmd(String processId) {
        this.processId = processId;
    }

    @Override
    public List<Map> execute(CommandContext commandContext) {
        List<Comment> list = CommandContextUtil.getCommentEntityManager(commandContext)
                .findCommentsByProcessInstanceId(processId);
        List<Map> comments=new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(o->{
                    CommentEntity model=(CommentEntity)o;
                    Map<String,Object> map=new HashMap<>();
                    map.put("createTime",sdf.format(model.getTime()));
                    map.put("message",model.getMessage());
                    comments.add(map);
        });
        return comments;
    }
}
