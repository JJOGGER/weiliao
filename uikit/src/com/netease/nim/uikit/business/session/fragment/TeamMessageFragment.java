package com.netease.nim.uikit.business.session.fragment;

import android.view.View;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.model.Team;

/**
 * Created by zhoujianghua on 2015/9/10.
 */
public class TeamMessageFragment extends MessageFragment {

    private Team team;

    @Override
    public boolean isAllowSendMessage(IMMessage message) {
        if (team == null) {
            team = NimUIKit.getTeamProvider().getTeamById(sessionId);
        }

        if (team == null || !team.isMyTeam()) {
            ToastHelper.showToast(getActivity(), R.string.team_send_message_not_allow);
            return false;
        }
        return super.isAllowSendMessage(message);
    }

    public void setTeam(Team team) {
        this.team = team;
        if (team == null) return;
        if (team.getCreator().equals(NimUIKit.getAccount())) return;
        if (team.isAllMute()) {
            inputPanel.messageInputBar.setVisibility(View.GONE);
            inputPanel.flForbidden.setVisibility(View.VISIBLE);
            messageListPanel.setForbidden(true);
        } else {
            inputPanel.messageInputBar.setVisibility(View.VISIBLE);
            inputPanel.flForbidden.setVisibility(View.GONE);
            messageListPanel.setForbidden(false);
        }
    }
}