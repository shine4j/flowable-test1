package com.ctgu.dao;

import com.ctgu.model.BO.TaskQueryBO;
import com.ctgu.model.BO.pager.PageQueryBO;
import com.ctgu.model.BO.pager.PagerModel;
import com.ctgu.model.VO.TaskVo;
import com.github.pagehelper.Page;

import java.util.Map;

/**
 * @Author beck_guo
 * @create 2022/5/28 11:46
 * @description
 */
public interface TaskDao {

    Page<Map> getMyTask(TaskQueryBO params);

    Page<Map> getMyHisTask(TaskQueryBO params);
}
