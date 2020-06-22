package com.netease.nim.uikit.business.session.helper;

import android.text.TextUtils;
import android.view.View;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.team.helper.TeamHelper;
import com.netease.nimlib.sdk.msg.attachment.NotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.constant.TeamAllMuteModeEnum;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.constant.VerifyTypeEnum;
import com.netease.nimlib.sdk.team.model.MemberChangeAttachment;
import com.netease.nimlib.sdk.team.model.MuteMemberAttachment;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.UpdateTeamAttachment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 系统消息描述文本构造器。主要是将各个系统消息转换为显示的文本内容。<br>
 * Created by huangjun on 2015/3/11.
 */
public class TeamNotificationHelper {
    private static ThreadLocal<String> teamId = new ThreadLocal<>();

    public static String getMsgShowText(final IMMessage message) {
        String content = "";
        String messageTip = message.getMsgType().getSendMessageTip();
        if (messageTip.length() > 0) {
            content += "[" + messageTip + "]";
        } else {
            if (message.getSessionType() == SessionTypeEnum.Team && message.getAttachment() != null) {
                content += getTeamNotificationText(message, message.getSessionId());
            } else {
                content += message.getContent();
            }
        }

        return content;
    }

    public static String getTeamNotificationText(IMMessage message, String tid) {
        return getTeamNotificationText(message.getSessionId(), message.getFromAccount(), (NotificationAttachment) message.getAttachment());
    }

    public static String getTeamNotificationText(String tid, String fromAccount, NotificationAttachment attachment) {
        teamId.set(tid);
        String text = buildNotification(tid, fromAccount, attachment);
        teamId.set(null);
        return text;
    }

    private static String buildNotification(String tid, String fromAccount, NotificationAttachment attachment) {
        String text;
        switch (attachment.getType()) {

            case InviteMember:
            case SUPER_TEAM_INVITE:
                text = buildInviteMemberNotification(((MemberChangeAttachment) attachment), fromAccount);
                break;
            case KickMember:
            case SUPER_TEAM_KICK:
                text = buildKickMemberNotification(((MemberChangeAttachment) attachment));
                break;
            case LeaveTeam:
            case SUPER_TEAM_LEAVE:
                text = buildLeaveTeamNotification(fromAccount);
                break;
            case DismissTeam:
            case SUPER_TEAM_DISMISS:
                text = buildDismissTeamNotification(fromAccount);
                break;
            case UpdateTeam:
            case SUPER_TEAM_UPDATE_T_INFO:
                text = buildUpdateTeamNotification(tid, fromAccount, (UpdateTeamAttachment) attachment);
                break;
            case PassTeamApply:
            case SUPER_TEAM_APPLY_PASS:
                text = buildManagerPassTeamApplyNotification((MemberChangeAttachment) attachment);
                break;
            case TransferOwner:
            case SUPER_TEAM_CHANGE_OWNER:
                text = buildTransferOwnerNotification(fromAccount, (MemberChangeAttachment) attachment);
                break;
            case AddTeamManager:
            case SUPER_TEAM_ADD_MANAGER:
                text = buildAddTeamManagerNotification((MemberChangeAttachment) attachment);
                break;
            case RemoveTeamManager:
            case SUPER_TEAM_REMOVE_MANAGER:
                text = buildRemoveTeamManagerNotification((MemberChangeAttachment) attachment);
                break;
            case AcceptInvite:
            case SUPER_TEAM_INVITE_ACCEPT:
                text = buildAcceptInviteNotification(fromAccount, (MemberChangeAttachment) attachment);
                break;
            case MuteTeamMember:
            case SUPER_TEAM_MUTE_TLIST:
                text = buildMuteTeamNotification((MuteMemberAttachment) attachment, fromAccount);
                break;
            default:
                text = getTeamMemberDisplayName(fromAccount) + ": unknown message";
                break;
        }

        return text;
    }

    private static String getTeamMemberDisplayName(String account) {
        return TeamHelper.getTeamMemberDisplayNameYou(teamId.get(), account);
    }

    private static String buildMemberListString(List<String> members, String fromAccount) {
        StringBuilder sb = new StringBuilder();
        for (String account : members) {
            if (!TextUtils.isEmpty(fromAccount) && fromAccount.equals(account)) {
                continue;
            }
            sb.append(getTeamMemberDisplayName(account));
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    private static String buildInviteMemberNotification(MemberChangeAttachment a, String fromAccount) {
        StringBuilder sb = new StringBuilder();
        String selfName = getTeamMemberDisplayName(fromAccount);

        sb.append(selfName);
        sb.append("邀请 ");
        sb.append(buildMemberListString(a.getTargets(), fromAccount));
        Team team = NimUIKit.getTeamProvider().getTeamById(teamId.get());
        if (team == null || team.getType() == TeamTypeEnum.Advanced) {
            sb.append(" 加入群");
        } else {
            sb.append(" 加入讨论组");
        }

        return sb.toString();
    }

    private static String buildKickMemberNotification(MemberChangeAttachment a) {
        Team team = NimUIKit.getTeamProvider().getTeamById(teamId.get());
        StringBuilder sb = new StringBuilder();
        ArrayList<String> members = a.getTargets();
        boolean containSelf = false;
        if (members.size() != 1) {
            for (String account : members) {
                if (account.equals(NimUIKit.getAccount())) {
                    containSelf = true;//存在关于自R身的信息
                    sb.append(getTeamMemberDisplayName(account));
                }
            }
        }else{
            if (members.get(0).equals(NimUIKit.getAccount())) {
                containSelf = true;//存在关于自身的信息
                sb.append(getTeamMemberDisplayName(members.get(0)));
            }
        }
        if (team == null) {
            sb.append(" 已被移出群");
            return sb.toString();
        }
        String creator = team.getCreator();
        if (!creator.equals(NimUIKit.getAccount()) && !containSelf) {
            //当前不是群主且不是被禁言人，不显示正确信息
            sb.append("群消息更新");
        } else if (containSelf) {
            if (team.getType() == TeamTypeEnum.Advanced) {
                sb.append(" 已被移出群");
                return sb.toString();
            }
        } else {
            sb.append(buildMemberListString(a.getTargets(), null));
            if (team.getType() == TeamTypeEnum.Advanced) {
                sb.append(" 已被移出群");
            } else {
                sb.append(" 已被移出讨论组");
            }
        }

        return sb.toString();
    }

    private static String buildLeaveTeamNotification(String fromAccount) {
        String tip;
        Team team = NimUIKit.getTeamProvider().getTeamById(teamId.get());
        if (team == null || team.getType() == TeamTypeEnum.Advanced) {
            tip = " 离开了群";
        } else {
            tip = " 离开了讨论组";
        }
        return getTeamMemberDisplayName(fromAccount) + tip;
    }

    private static String buildDismissTeamNotification(String fromAccount) {
        return getTeamMemberDisplayName(fromAccount) + " 解散了群";
    }

    private static String buildUpdateTeamNotification(String tid, String account, UpdateTeamAttachment a) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<TeamFieldEnum, Object> field : a.getUpdatedFields().entrySet()) {
            if (field.getKey() == TeamFieldEnum.Name) {
                sb.append("名称被更新为 " + field.getValue());
            } else if (field.getKey() == TeamFieldEnum.Introduce) {
                sb.append("群介绍被更新为 " + field.getValue());
            } else if (field.getKey() == TeamFieldEnum.Announcement) {
                sb.append(TeamHelper.getTeamMemberDisplayNameYou(tid, account) + " 修改了群公告");
            } else if (field.getKey() == TeamFieldEnum.VerifyType) {
                VerifyTypeEnum type = (VerifyTypeEnum) field.getValue();
                String authen = "群身份验证权限更新为";
                if (type == VerifyTypeEnum.Free) {
                    sb.append(authen + NimUIKit.getContext().getString(R.string.team_allow_anyone_join));
                } else if (type == VerifyTypeEnum.Apply) {
                    sb.append(authen + NimUIKit.getContext().getString(R.string.team_need_authentication));
                } else {
                    sb.append(authen + NimUIKit.getContext().getString(R.string.team_not_allow_anyone_join));
                }
            } else if (field.getKey() == TeamFieldEnum.Extension) {
                sb.append("群扩展字段被更新为 " + field.getValue());
            } else if (field.getKey() == TeamFieldEnum.Ext_Server_Only) {
                sb.append("群扩展字段(服务器)被更新为 " + field.getValue());
            } else if (field.getKey() == TeamFieldEnum.ICON) {
                sb.append("群头像已更新");
            } else if (field.getKey() == TeamFieldEnum.InviteMode) {
                sb.append("群邀请他人权限被更新为 " + field.getValue());
            } else if (field.getKey() == TeamFieldEnum.TeamUpdateMode) {
                sb.append("群资料修改权限被更新为 " + field.getValue());
            } else if (field.getKey() == TeamFieldEnum.BeInviteMode) {
                sb.append("群被邀请人身份验证权限被更新为 " + field.getValue());
            } else if (field.getKey() == TeamFieldEnum.TeamExtensionUpdateMode) {
                sb.append("群扩展字段修改权限被更新为 " + field.getValue());
            } else if (field.getKey() == TeamFieldEnum.AllMute) {
                TeamAllMuteModeEnum teamAllMuteModeEnum = (TeamAllMuteModeEnum) field.getValue();
                if (teamAllMuteModeEnum == TeamAllMuteModeEnum.Cancel) {
                    sb.append("取消群全员禁言");
                } else {
                    sb.append("群全员禁言");
                }
            } else {
                sb.append("群" + field.getKey() + "被更新为 " + field.getValue());
            }
            sb.append("\r\n");
        }
        if (sb.length() < 2) {
            return "未知通知";
        }
        return sb.delete(sb.length() - 2, sb.length()).toString();
    }

    private static String buildManagerPassTeamApplyNotification(MemberChangeAttachment a) {
        StringBuilder sb = new StringBuilder();
        sb.append("管理员通过用户 ");
        sb.append(buildMemberListString(a.getTargets(), null));
        sb.append(" 的入群申请");

        return sb.toString();
    }

    private static String buildTransferOwnerNotification(String from, MemberChangeAttachment a) {
        StringBuilder sb = new StringBuilder();
        sb.append(getTeamMemberDisplayName(from));
        sb.append(" 将群转移给 ");
        sb.append(buildMemberListString(a.getTargets(), null));

        return sb.toString();
    }

    private static String buildAddTeamManagerNotification(MemberChangeAttachment a) {
        StringBuilder sb = new StringBuilder();

        sb.append(buildMemberListString(a.getTargets(), null));
        sb.append(" 被任命为管理员");

        return sb.toString();
    }

    private static String buildRemoveTeamManagerNotification(MemberChangeAttachment a) {
        StringBuilder sb = new StringBuilder();

        sb.append(buildMemberListString(a.getTargets(), null));
        sb.append(" 被撤销管理员身份");

        return sb.toString();
    }

    private static String buildAcceptInviteNotification(String from, MemberChangeAttachment a) {
        StringBuilder sb = new StringBuilder();

        sb.append(getTeamMemberDisplayName(from));
        sb.append(" 接受了 ").append(buildMemberListString(a.getTargets(), null)).append(" 的入群邀请");

        return sb.toString();
    }

    private static String buildMuteTeamNotification(MuteMemberAttachment a, String fromAccount) {
        Team team = NimUIKit.getTeamProvider().getTeamById(teamId.get());
        StringBuilder sb = new StringBuilder();
        ArrayList<String> members = a.getTargets();
        boolean containSelf = false;
        if (members.size() != 1) {
            for (String account : members) {
                if (account.equals(NimUIKit.getAccount())) {
                    containSelf = true;//存在关于自身的信息
                    sb.append(getTeamMemberDisplayName(account));
                }
            }
        }else{
            if (members.get(0).equals(NimUIKit.getAccount())) {
                containSelf = true;//存在关于自身的信息
                sb.append(getTeamMemberDisplayName(members.get(0)));
            }
        }
        if (team == null) {
            sb.append(" 已被移出群,无法接收新消息");
            return sb.toString();
        }
        String creator = team.getCreator();
        if (!creator.equals(NimUIKit.getAccount()) && !containSelf) {
            //当前不是群主且不是被禁言人，不显示正确信息
            sb.append("群消息更新");
        } else if (containSelf) {
            sb.append("已被管理员");
            sb.append(a.isMute() ? "禁言" : "解除禁言");
            return sb.toString();
        } else {
            sb.append(buildMemberListString(a.getTargets(), null));
            sb.append("已被管理员");
            sb.append(a.isMute() ? "禁言" : "解除禁言");
        }
        return sb.toString();
    }
}
