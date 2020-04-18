package com.diamond.jogger.base.entity;

import com.diamond.jogger.base.utils.GsonUtil;

/**
 * Created by jogger on 2019/12/10
 * 描述：
 */
public class TeamActionInfo {
    private int isForbidden;
    private int isAllowSingleChat;

    public int isForbidden() {
        return isForbidden;
    }

    public void setForbidden(int forbidden) {
        isForbidden = forbidden;
    }

    public int isAllowSingleChat() {
        return isAllowSingleChat;
    }

    public void setAllowSingleChat(int allowSingleChat) {
        isAllowSingleChat = allowSingleChat;
    }

    public static TeamActionInfo fromGson(String json) {
        TeamActionInfo teamActionInfo;
        try {
            teamActionInfo = GsonUtil.fromJson(json, TeamActionInfo.class);
            if (teamActionInfo == null) {
                teamActionInfo = new TeamActionInfo();
            }
        } catch (Exception e) {
            teamActionInfo = new TeamActionInfo();
        }

        return teamActionInfo;
    }

    public static String toGson(TeamActionInfo teamActionInfo) {
        return GsonUtil.toJson(teamActionInfo);
    }
}
