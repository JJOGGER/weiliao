package com.netease.nim.uikit.business.contact.core.item;

public class GroupTopItem extends AbsContactItem {
    private final String text;
    private boolean isHide=true;
    private int position=0;
    private int size;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public GroupTopItem(String text) {
        this.text = text;
    }
    public GroupTopItem(String text, int position, int size) {
        this.text = text;
        this.position=position;
        this.size=size;
    }
    @Override
    public int getItemType() {
        return ItemTypes.GROUP_TOP;
    }

    @Override
    public String belongsGroup() {
        return null;
    }

    public final String getText() {
        return text;
    }
}
