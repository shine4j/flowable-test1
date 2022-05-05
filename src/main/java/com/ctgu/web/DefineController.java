package com.ctgu.web;

import com.ctgu.BO.ResultMsgBO;
import com.ctgu.service.IDefineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author beck_guo
 * @create 2022/5/5 17:33
 * @description
 */
@RestController
@RequestMapping("api/define")
public class DefineController {

    @Autowired
    private IDefineService iDefineService;

    @GetMapping("getAllDefine")
    public ResultMsgBO getAllDefine(){
        return iDefineService.getAllDefine();
    }
}
