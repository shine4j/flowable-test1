package com.ctgu.dao;

import java.util.List;

/**
 * @Author beck_guo
 * @create 2022/4/22 14:15
 * @description
 */
public interface RunFlowableActinstDao {

    /**
     * 删除节点信息
     * @param ids ids
     */
    void deleteRunActinstsByIds(List<String> ids) ;
}
