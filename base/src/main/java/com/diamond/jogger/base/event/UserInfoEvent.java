package com.diamond.jogger.base.event;

/**
 * Created by jogger on 2020/1/2
 * 描述：
 */
public class UserInfoEvent extends BaseActionEvent{
    public static final int ACTION_USER_INFO_UPDATE=10;


    public UserInfoEvent(int action) {
        super(action);
    }
}
