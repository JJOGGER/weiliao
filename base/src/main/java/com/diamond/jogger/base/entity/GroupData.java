package com.diamond.jogger.base.entity;

import java.io.Serializable;

/**
 * Created by jogger on 2019/12/12
 * 描述：
 */
public class GroupData implements Serializable {
    private String id;
    private String defaultFlag;
    private String groupName;

    private boolean isSelected;

    public String isDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(String defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public GroupData(String id, String groupName) {
        this.id = id;
        this.groupName = groupName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
