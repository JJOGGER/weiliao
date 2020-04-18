package com.diamond.jogger.base.event;

/**
 * Created by jogger on 2019/12/5
 * 描述：
 */
public class GroupForbiddenEvent extends BaseActionEvent{
    public static final int FORBIDDEN = 100;
    public static final int UN_FORBIDDEN = 200;

    public GroupForbiddenEvent(int action) {
        super(action);
    }
}
