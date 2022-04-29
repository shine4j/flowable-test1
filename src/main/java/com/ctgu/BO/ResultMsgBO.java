package com.ctgu.BO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author beck_guo
 * @create 2022/4/29 15:19
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultMsgBO<T> {

    private Integer code;

    private String msg;

    private T data;
}
