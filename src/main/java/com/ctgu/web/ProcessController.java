package com.ctgu.web;

import com.ctgu.model.BO.ProcessQueryBO;
import com.ctgu.model.BO.ResultMsgBO;
import com.ctgu.model.BO.pager.PageQueryBO;
import com.ctgu.model.BO.pager.PagerModel;
import com.ctgu.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    @GetMapping(value = "getApplyNodes/{processInstanceId}")
    public ResultMsgBO getApplyNodes(@PathVariable String processInstanceId) {
        return iProcessService.applyNodes(processInstanceId);
    }

    @GetMapping(value = "getNoFish")
    public ResultMsgBO noFish(ProcessQueryBO parms, PageQueryBO query) {
        PagerModel<Map> processNoFish = iProcessService.getNoFish(parms, query);
        return  new ResultMsgBO(0,"ok",processNoFish);
    }

    @GetMapping(value = "getFish")
    public ResultMsgBO fish(ProcessQueryBO parms, PageQueryBO query) {
        PagerModel<Map> processFish = iProcessService.getFish(parms, query);
        return  new ResultMsgBO(0,"ok",processFish);
    }

    @GetMapping(value = "getComment/{processInstanceId}")
    public ResultMsgBO getComment(@PathVariable String processInstanceId) {
        List<Map> comment = iProcessService.getComment(processInstanceId);
        return  new ResultMsgBO(0,"ok",comment);
    }

}
