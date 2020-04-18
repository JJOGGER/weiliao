package com.diamond.jogger.base.entity;

/**
 * Created by jogger on 2019/12/18
 * 描述：
 */
public interface RedPacketConstant {
    interface RedStatus {
        String STATUS_ACCEPT_NO = "acceptNo";
        String STATUS_ACCEPT_YES = "acceptYes";
        String STATUS_ACCEPT_OVER = "acceptOver";
        String STATUS_ACCEPT_BACK = "refund";//已退款
    }

    interface RedType {
        String TYPE_NORMAL = "groupNormal";
        String TYPE_LUCKY = "groupLuck";
    }
}
