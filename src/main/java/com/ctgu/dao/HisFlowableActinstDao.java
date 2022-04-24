package com.ctgu.dao;

import java.util.List;

/**
 * @Author beck_guo
 * @create 2022/4/22 14:17
 * @description
 */
public interface HisFlowableActinstDao {

    /**
     * 删除节点信息
     * @param ids ids
     */
     void deleteHisActinstsByIds(List<String> ids) ;
}
