package com.ctgu.web;

import com.ctgu.BO.ResultMsgBO;
import com.ctgu.service.IModerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("api/moder")
public class ModerController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected IModerService iModerService;

    @GetMapping(value = "loadPngByModelId/{modeId}")
    public void getImageById(@PathVariable String modeId, HttpServletResponse resp) throws IOException {
        iModerService.getImageById(modeId,resp);
    }

    @GetMapping("loadXmlByModelId/{modeId}")
    public void loadXmlByModelId(@PathVariable String modeId, HttpServletResponse resp){
        iModerService.getXmlById(modeId,resp);
    }

    @GetMapping("getModerAll")
    public ResultMsgBO getModerAll(){
        return iModerService.getAllModer();
    }

    @GetMapping("deploy/{moderId}")
    public ResultMsgBO deployModerById(@PathVariable String moderId){
        return iModerService.deployModerById(moderId);
    }
}
