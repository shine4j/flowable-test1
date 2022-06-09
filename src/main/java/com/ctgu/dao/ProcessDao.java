package com.ctgu.dao;

import com.ctgu.model.BO.ProcessQueryBO;
import com.ctgu.model.BO.TaskQueryBO;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

/**
 * @Author beck_guo
 * @create 2022/5/28 11:46
 * @description
 */
public interface ProcessDao {

    Page<Map> getNoFish(ProcessQueryBO params);

    Page<Map> getFish(ProcessQueryBO params);

    List<Map> applyNodes(String processId);
}
