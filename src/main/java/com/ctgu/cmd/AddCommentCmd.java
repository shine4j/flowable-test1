package com.ctgu.cmd;

import com.ctgu.model.PO.RolePO;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.persistence.entity.CommentEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.task.Comment;
import org.flowable.identitylink.service.impl.persistence.entity.HistoricIdentityLinkEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author beck_guo
 * @create 2022/4/26 11:38
 * @description
 */

public class AddCommentCmd implements Command<Comment> {

    Logger logger = LoggerFactory.getLogger(getClass());

    private String processId;

    private String taskId;

    private String userId;

    private String message;

    public AddCommentCmd(String processId, String taskId, String userId, String message) {
        this.processId = processId;
        this.taskId = taskId;
        this.userId = userId;
        this.message = message;
    }

    @Override
    public Comment execute(CommandContext commandContext) {
        CommentEntity comment = CommandContextUtil.getCommentEntityManager(commandContext).create();
        comment.setUserId(userId);
        comment.setProcessInstanceId(processId);
        comment.setTaskId(taskId);
        comment.setMessage(message);
        CommandContextUtil.getCommentEntityManager(commandContext)
                .insert(comment);
        return comment;
    }
}
