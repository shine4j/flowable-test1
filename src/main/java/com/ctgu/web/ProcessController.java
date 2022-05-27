package com.ctgu.web;

import com.ctgu.model.BO.ResultMsgBO;
import com.ctgu.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author beck_guo
 * @create 2022/4/12 13:38
 * @description
 */
@RestController
@RequestMapping("api/process")
public class ProcessController {

    @Autowired
    protected IProcessService iProcessService;

    @PostMapping(value = "/image/{processInstanceId}")
    public void getImage(@PathVariable String processInstanceId, HttpServletResponse response) throws IOException {
        iProcessService.getImage(processInstanceId,response);
    }


    @GetMapping(value = "getStart/{username}")
    public ResultMsgBO getStart(@PathVariable String username) {
       return iProcessService.getStart(username);
    }

    @GetMapping(value = "start/{key}/{username}")
    public ResultMsgBO startByKey(@PathVariable String key,@PathVariable String username) {
        return iProcessService.startByKey(key,username);
    }

    @GetMapping(value = "getApplyNodes/{processInstanceId}")
    public ResultMsgBO getApplyNodes(@PathVariable String processInstanceId) {
        return iProcessService.applyNodes(processInstanceId);
    }

    @GetMapping(value = "getNoFish")
    public ResultMsgBO noFish() {
        return iProcessService.getNoFish();
    }

    @GetMapping(value = "getFish")
    public ResultMsgBO fish() {
        return iProcessService.getFish();
    }

}
