package com.netease.nim.uikit.impl.provider;

import android.text.TextUtils;
import android.util.Log;

import com.diamond.jogger.base.entity.GroupData;
import com.diamond.jogger.base.utils.GsonUtil;
import com.google.gson.Gson;
import com.netease.nim.uikit.api.model.contact.ContactProvider;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.impl.cache.FriendDataCache;
import com.netease.nimlib.sdk.friend.model.Friend;

import java.util.List;
import java.util.Map;

/**
 * UIKit默认的通讯录（联系人）数据源提供者，
 * Created by hzchenkang on 2016/12/19.
 */

public class DefaultContactProvider implements ContactProvider {

    @Override
    public List<String> getUserInfoOfMyFriends() {
        return FriendDataCache.getInstance().getMyFriendAccounts();
    }

    @Override
    public int getMyFriendsCount() {
        return FriendDataCache.getInstance().getMyFriendCounts();
    }

    @Override
    public String getAlias(String account) {
        Friend friend = FriendDataCache.getInstance().getFriendByAccount(account);
        if (friend != null && !TextUtils.isEmpty(friend.getAlias())) {
            return friend.getAlias();
        }
        return null;
    }

    @Override
    public boolean isMyFriend(String account) {
        return FriendDataCache.getInstance().isMyFriend(account);
    }

    @Override
    public GroupData getGroupData(String account) {
        Friend friend = FriendDataCache.getInstance().getFriendByAccount(account);
        if (friend == null) return null;
        Map<String, Object> extension = friend.getExtension();
        if (extension == null) return null;
        Object ext = extension.get("ext");
        if (ext == null) return null;
        GroupData groupData = GsonUtil.fromJson(ext.toString(), GroupData.class);
        return groupData;
    }
}
