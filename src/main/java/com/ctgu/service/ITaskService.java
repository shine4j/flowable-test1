package com.ctgu.service;

import com.ctgu.BO.ResultMsgBO;
import com.ctgu.BO.TaskHandleBO;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;

/**
 * @Author beck_guo
 * @create 2022/4/29 15:16
 * @description
 */
public interface ITaskService {

    /**
     * 查询所有任务
     */
    ResultMsgBO getTask(String username);

    /**
     * 通过任务
     */
    ResultMsgBO doComplete(TaskHandleBO model);

    /**
     * 回退任务
     */
    ResultMsgBO doBack(String taskId, String distFlowElementId);

    /**
     * 委派任务
     * A由于某些原因不能处理该任务，可以把任务委派给用户B代理
     * 当B处理完成之后再次回到用户A这里
     * 在这个过程中A是任务的所有者
     * B是该任务的办理人
     */
    ResultMsgBO doDelegateTask(String taskId, String userId);

    /**
     * 已审批任务
     */
    ResultMsgBO getHisTask();

    /**
     *沟通
     */
    ResultMsgBO addSign(TaskHandleBO model);


    /**
     *正在运行的任务
     */
    ResultMsgBO taskIng();


    /**
     *修改任务处理人
     */
    ResultMsgBO setAssignee(TaskHandleBO model);

}
