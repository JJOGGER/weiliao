package com.diamond.jogger.base.entity;

import java.util.List;

/**
 * Created by jogger on 2020/1/4
 * 描述：
 */
public class IntoRecordResponse {
    private int current;
    private int page;
    private List<IntoRecord> records;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<IntoRecord> getRecords() {
        return records;
    }

    public void setRecords(List<IntoRecord> records) {
        this.records = records;
    }
}
