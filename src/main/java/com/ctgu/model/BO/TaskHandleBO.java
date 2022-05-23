package com.ctgu.model.BO;

import lombok.Data;

import java.util.List;

/**
 * @Author beck_guo
 * @create 2022/5/11 12:01
 * @description
 */
@Data
public class TaskHandleBO {

    private String type;

    private String taskId;

    private String proccessId;

    private List<String> users;

    private String assign;
}
