package com.ctgu.dao;

import com.ctgu.model.PO.BaseBusinessPO;

/**
 * @Author beck_guo
 * @create 2022/6/15 10:08
 * @description 业务处理基础类
 */
public interface BaseBusinessDao<T extends BaseBusinessPO> {

    void save(T model);

    void del(Integer id);

    void update(T model);

    T getById(Integer id);
}
