package com.ctgu.service;

import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author beck_guo
 * @create 2022/4/29 15:31
 * @description
 */
public interface IProcessService {

    //得到实例的流程图
    void getImage(String processInstanceId, HttpServletResponse response) throws IOException;
}
