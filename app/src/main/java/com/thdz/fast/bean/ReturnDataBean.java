package com.thdz.fast.bean;

import java.io.Serializable;
import java.util.List;

/**
 * desc:    接口返回数据，if data是列表数据
 * author:  Administrator
 * date:    2018/11/16  11:09
 */
public class ReturnDataBean<T> implements Serializable {

    private int rowCount;
    private int pageSize;
    private int pageIndex;
    private List<T> pageData;

    public ReturnDataBean() {

    }

    public ReturnDataBean(int rowCount, int pageSize, int pageIndex, List<T> pageData) {
        this.rowCount = rowCount;
        this.pageSize = pageSize;
        this.pageIndex = pageIndex;
        this.pageData = pageData;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public List<T> getPageData() {
        return pageData;
    }

    public void setPageData(List<T> pageData) {
        this.pageData = pageData;
    }

    @Override
    public String toString() {
        return "ReturnDataBean{" +
                "rowCount=" + rowCount +
                ", pageSize=" + pageSize +
                ", pageIndex=" + pageIndex +
                ", pageData=" + pageData +
                '}';
    }
}
