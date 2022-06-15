package com.ctgu.model.PO;

import lombok.Data;

/**
 * @Author beck_guo
 * @create 2022/6/15 10:29
 * @description 请假实体类
 */
@Data
public class LeavePO extends BaseBusinessPO{

    private String startTime;

    private String endTime;

    private String remark;

    private String userId;


}
