package com.ctgu.model.BO.pager;

import com.github.pagehelper.Page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author beck_guo
 * @create 2022/5/28 11:44
 * @description
 */
public class PagerModel<T> implements Serializable {
    private static final long serialVersionUID = 4804053559968742915L;
    private long total;
    private List<T> data = new ArrayList();
    private List<T> rows = new ArrayList();

    public PagerModel() {
    }

    public PagerModel(List<T> list) {
        if (list instanceof Page) {
            Page<T> page = (Page)list;
            this.data = page;
            this.total = page.getTotal();
        } else if (list instanceof Collection) {
            this.data = list;
            this.total = (long)list.size();
        }

    }

    public PagerModel(long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
        this.data = rows;
    }

    public long getTotal() {
        return this.total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getData() {
        return this.data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public List<T> getRows() {
        return this.rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
