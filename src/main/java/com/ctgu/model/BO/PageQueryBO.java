package com.ctgu.model.BO;

import lombok.Data;

/**
 * @Author beck_guo
 * @create 2022/4/29 14:39
 * @description
 */
@Data
public class PageQueryBO {

    private int pageIndex;
    private int pageSize = 20;
    private int pageNum;
}
