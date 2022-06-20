package com.ctgu.web;

import com.ctgu.model.BO.ResultMsgBO;
import com.ctgu.model.PO.flow.LeavePO;
import com.ctgu.service.flow.ILeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author beck_guo
 * @create 2022/6/18 9:32
 * @description
 */
@RestController
@RequestMapping("leave")
public class LeaveController {

    @Autowired
    private ILeaveService leaveService;

    @GetMapping("getById/{id}")
    public ResultMsgBO getById(@PathVariable Integer id){
        LeavePO po = leaveService.getById(id);
        return new ResultMsgBO(0,"ok",po);
    }
}
