package com.ctgu.model.BO;

import lombok.Data;

/**
 * @Author beck_guo
 * @create 2022/5/28 8:48
 * @description 启动流程实例
 */
@Data
public class AddFlowBO {
    private String key;
    private String username;
    private String subject;
    private Object flow;
}
