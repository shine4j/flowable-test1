package com.ctgu.model.VO;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author beck_guo
 * @create 2022/5/30 11:45
 * @description
 */
@Data
public class TaskVo implements Serializable {

    /**
     * 任务id
     */
    private String taskId;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 审批人
     */
    private String approver;
    /**
     * 审批人id
     */
    private String approverId;
    /**
     * 表单名称
     */
    private String formName;
    /**
     * 业务主键
     */
    private String businessKey;
    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 开始时间
     */
    private Date startTime ;

    /**
     * 结束时间
     */
    private Date endTime;
}
