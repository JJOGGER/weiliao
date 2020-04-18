package com.netease.nim.uikit.business.contact.core.item;

import java.util.List;

public class GroupTopModel {
    private String groupName;
    private String id;
    private List<ContactItem> mItemList;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ContactItem> getItemList() {
        return mItemList;
    }

    public void setItemList(List<ContactItem> itemList) {
        mItemList = itemList;
    }
}
