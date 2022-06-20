package com.ctgu.service.flow.impl;

import com.ctgu.dao.LeaveDao;
import com.ctgu.model.PO.flow.LeavePO;
import com.ctgu.service.flow.ILeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author beck_guo
 * @create 2022/6/18 9:30
 * @description
 */
@Service
public class LeaveServiceImpl implements ILeaveService {

    @Autowired
    private LeaveDao leaveDao;

    @Override
    public LeavePO getById(Integer id) {
        return leaveDao.getById(id);
    }

    @Override
    public void update(LeavePO model) {
        leaveDao.update(model);
    }
}
