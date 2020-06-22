package com.netease.nim.uikit.business.contact.core.provider;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.diamond.jogger.base.entity.GroupData;
import com.diamond.jogger.base.http.datasource.UserDataSource;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.business.contact.core.item.ContactGroupTopItem;
import com.netease.nim.uikit.business.contact.core.item.ContactItem;
import com.netease.nim.uikit.business.contact.core.item.GroupTopModel;
import com.netease.nim.uikit.business.contact.core.item.ItemTypes;
import com.netease.nim.uikit.business.contact.core.query.TextQuery;
import com.netease.nim.uikit.business.contact.core.util.ContactHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.impl.cache.UIKitLogTag;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.FriendFieldEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class UserDataProvider {

    private static String sContent;

    public static List<AbsContactItem> provide(TextQuery query) {
        List<UserInfo> sources = query(query);
        List<AbsContactItem> items = new ArrayList<>(sources.size());
        for (UserInfo u : sources) {
            items.add(new ContactItem(ContactHelper.makeContactFromUserInfo(u), ItemTypes.FRIEND));
        }

        LogUtil.i(UIKitLogTag.CONTACT, "contact provide data size =" + items.size());
        return items;
    }

    public static List<AbsContactItem> provide2(TextQuery query) {
        List<NimUserInfo> sources = query2(query);
        List<AbsContactItem> items = new ArrayList<>(sources.size());
        List<GroupTopModel> groupTopModels = new ArrayList<>();
        if (UserDataSource.getGroupDatasCache().isEmpty()) {
            //默认分组
            List<ContactItem> list = new ArrayList<>();
            for (NimUserInfo userInfo :
                    sources) {
                list.add(new ContactItem(ContactHelper.makeContactFromUserInfo(userInfo), ItemTypes.FRIEND));
            }
            GroupTopModel groupTopModel = new GroupTopModel();
            groupTopModel.setId(null);
            groupTopModel.setGroupName("我的好友");
            groupTopModel.setItemList(list);
            groupTopModels.add(groupTopModel);
            items.add(new ContactGroupTopItem(ContactHelper.makeContactFromUserInfo(groupTopModels.get(0).getGroupName()), ItemTypes.GROUP_TOP, groupTopModels.get(0).getItemList()));
            return items;
        }
        sContent = "s:" + UserDataSource.getGroupDatasCache().size();
        for (int i = 0; i < UserDataSource.getGroupDatasCache().size(); i++) {
            GroupData groupDataCache = UserDataSource.getGroupDatasCache().get(i);
            GroupTopModel groupTopModel = new GroupTopModel();
            groupTopModel.setGroupName(groupDataCache.getGroupName());
            groupTopModel.setId(groupDataCache.getId());
            List<ContactItem> list = new ArrayList<>();
            for (Iterator<NimUserInfo> iterator = sources.iterator(); iterator.hasNext(); ) {
                NimUserInfo next = iterator.next();
                GroupData groupData = NimUIKit.getContactProvider().getGroupData(next.getAccount());
                if (groupData == null) {//没有设分组
                    list.add(new ContactItem(ContactHelper.makeContactFromUserInfo(next), ItemTypes.FRIEND));
                    iterator.remove();
                } else {
                    if (UserDataSource.getGroupDatasCache().get(i).getId().equals(groupData.getId())) {
                        //找到分组
                        list.add(new ContactItem(ContactHelper.makeContactFromUserInfo(next), ItemTypes.FRIEND));
                        iterator.remove();
                    } else {
                        //用户设了分组，但是分组不存在于服务器,则把用户归类为默认分组
                        boolean existInGroup = false;
                        for (int j = 0; j < UserDataSource.getGroupDatasCache().size(); j++) {
                            GroupData gd = UserDataSource.getGroupDatasCache().get(j);
                            if (gd.getId().equals(groupData.getId())) {
                                existInGroup = true;
                                break;
                            }
                        }
                        if (!existInGroup) {
                            Map<FriendFieldEnum, Object> map = new HashMap<>();
                            Map<String, Object> exts = new HashMap<>();
                            exts.put("ext", "");
                            map.put(FriendFieldEnum.EXTENSION, exts);
                            NIMClient.getService(FriendService.class).updateFriendFields(next.getAccount(), map);
                            list.add(new ContactItem(ContactHelper.makeContactFromUserInfo(next), ItemTypes.FRIEND));
                            iterator.remove();
                        }
                    }
                }

            }
            sContent += " 找到items " + list.size() + "-->" + groupTopModel.getGroupName() + "  ";
            groupTopModel.setItemList(list);
            groupTopModels.add(groupTopModel);
        }
        Collections.sort(groupTopModels, mComparator);
        for (int i = 0; i < groupTopModels.size(); i++) {
            items.add(new ContactGroupTopItem(ContactHelper.makeContactFromUserInfo(groupTopModels.get(i).getGroupName()), ItemTypes.FRIEND, groupTopModels.get(i).getItemList()));
        }
        LogUtil.i(UIKitLogTag.CONTACT, "contact provide data size =" + items.size());
        return items;
    }

    private static Comparator mComparator = new Comparator<GroupTopModel>() {
        @Override
        public int compare(GroupTopModel o1, GroupTopModel o2) {
            if (TextUtils.isEmpty(o1.getId()))
                return 1;
            else {
                return Long.valueOf(o1.getId()) - Long.valueOf(o2.getId()) > 0 ? 1 : -1;
            }
        }
    };

    private static final List<NimUserInfo> query2(TextQuery query) {

        List<String> friends = NimUIKit.getContactProvider().getUserInfoOfMyFriends();
        List<NimUserInfo> users = NimUIKit.getUserInfoProvider().getUserInfo(friends);
        if (query == null) {
            return users;
        }

        UserInfo user;
        for (Iterator<NimUserInfo> iter = users.iterator(); iter.hasNext(); ) {
            user = iter.next();
            boolean hit = ContactSearch.hitUser(user, query) || (ContactSearch.hitFriend(user, query));
            if (!hit) {
                iter.remove();
            }
        }
        return users;
    }

    private static final List<UserInfo> query(TextQuery query) {

        List<String> friends = NimUIKit.getContactProvider().getUserInfoOfMyFriends();
        List<UserInfo> users = NimUIKit.getUserInfoProvider().getUserInfo(friends);
        if (query == null) {
            return users;
        }

        UserInfo user;
        for (Iterator<UserInfo> iter = users.iterator(); iter.hasNext(); ) {
            user = iter.next();
            boolean hit = ContactSearch.hitUser(user, query) || (ContactSearch.hitFriend(user, query));
            if (!hit) {
                iter.remove();
            }
        }
        return users;
    }
}