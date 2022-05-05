package com.ctgu.web;

import com.ctgu.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author beck_guo
 * @create 2022/4/12 13:38
 * @description
 */
@RestController
@RequestMapping("process")
public class ProcessController {

    @Autowired
    protected IProcessService iProcessService;

    @GetMapping(value = "/image/{processInstanceId}")
    public void getImage(@PathVariable String processInstanceId, HttpServletResponse response) throws IOException {
        iProcessService.getImage(processInstanceId,response);
    }

    @GetMapping(value = "/stop/{processInstanceId}")
    public void doStopProcess(@PathVariable String processInstanceId, HttpServletResponse response) {
        iProcessService.doStopProcess(processInstanceId);
    }
}
