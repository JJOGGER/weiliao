package com.diamond.jogger.base.event;

public class GroupChangeEvent extends BaseActionEvent {
    public static final int ADD = 10;
    public static final int UPDATE = 20;
    public static final int DELETE = 20;
    public GroupChangeEvent(int action) {
        super(action);
    }
}
