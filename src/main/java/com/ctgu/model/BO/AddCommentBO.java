package com.ctgu.model.BO;

import lombok.Data;

/**
 * @Author beck_guo
 * @create 2022/6/10 9:44
 * @description 新增审批意见
 */
@Data
public class AddCommentBO {

    private String processId;

    private String taskId;

    private String userId;

    private String message;
}
