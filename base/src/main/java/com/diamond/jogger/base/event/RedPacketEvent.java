package com.diamond.jogger.base.event;

/**
 * Created by jogger on 2019/12/18
 * 描述：
 */
public class RedPacketEvent extends BaseActionEvent {
    public static final int ACTION_SEND_RED_PACKET = 10;
    public static final int ACTION_READ_RED_PACKET = 20;

    public RedPacketEvent(int action) {
        super(action);
    }
}
