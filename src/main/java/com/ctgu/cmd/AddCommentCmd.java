package com.ctgu.cmd;

import com.ctgu.model.BO.AddCommentBO;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.persistence.entity.CommentEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.task.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


/**
 * @Author beck_guo
 * @create 2022/4/26 11:38
 * @description
 */

public class AddCommentCmd implements Command<Comment> {

    private AddCommentBO commentBO;

    public AddCommentCmd(AddCommentBO commentBO) {
        this.commentBO = commentBO;
    }

    @Override
    public Comment execute(CommandContext commandContext) {
        CommentEntity comment = CommandContextUtil.getCommentEntityManager(commandContext).create();
        comment.setUserId(commentBO.getUserId());
        comment.setProcessInstanceId(commentBO.getProcessId());
        comment.setTaskId(commentBO.getTaskId());
        comment.setMessage(commentBO.getMessage());
        comment.setTime(new Date());
        CommandContextUtil.getCommentEntityManager(commandContext)
                .insert(comment);
        return comment;
    }
}
