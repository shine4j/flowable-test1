package com.ctgu.service.flow;

import com.ctgu.model.PO.flow.LeavePO;

/**
 * @Author beck_guo
 * @create 2022/6/18 9:28
 * @description
 */
public interface ILeaveService {

    LeavePO getById(Integer id);

    void update(LeavePO model);
}
