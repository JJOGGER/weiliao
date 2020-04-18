package com.netease.nim.demo.main.fragment;

import android.os.Bundle;

import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.contact.activity.BlackListActivity;
import com.netease.nim.demo.main.activity.RobotListActivity;
import com.netease.nim.demo.main.activity.SystemMessageActivity;
import com.netease.nim.demo.main.activity.TeamListActivity;
import com.netease.nim.demo.main.model.MainTab;
import com.netease.nim.demo.session.SessionHelper;
import com.netease.nim.uikit.business.contact.core.item.ItemTypes;
import com.netease.nim.uikit.business.contact.core.model.FuncViewHolder;
import com.netease.nim.uikit.business.contact.core.model.FunctionViewHolder;
import com.netease.nim.uikit.api.model.contact.ContactsCustomization;
import com.netease.nim.uikit.business.contact.ContactsExFragment;
import com.netease.nim.uikit.business.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.business.contact.core.viewholder.AbsContactViewHolder;
import com.netease.nim.uikit.common.activity.UI;

import java.util.List;


/**
 * 集成通讯录列表
 * <p/>
 * Created by huangjun on 2015/9/7.
 */
public class ContactListFragment extends MainTabFragment {

    private ContactsExFragment fragment;

    public ContactListFragment() {
        setContainerId(MainTab.CONTACT.fragmentId);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        onCurrent(); // 触发onInit，提前加载
    }

    @Override
    protected void onInit() {
        addContactFragment();  // 集成通讯录页面
    }

    // 将通讯录列表fragment动态集成进来。 开发者也可以使用在xml中配置的方式静态集成。
    private void addContactFragment() {
        fragment = new ContactsExFragment();
        fragment.setContainerId(R.id.contact_fragment);

        UI activity = (UI) getActivity();

        // 如果是activity从堆栈恢复，FM中已经存在恢复而来的fragment，此时会使用恢复来的，而new出来这个会被丢弃掉
        fragment = (ContactsExFragment) activity.addFragment(fragment);

        // 功能项定制
        fragment.setContactsCustomization(new ContactsCustomization() {
            @Override
            public Class<? extends AbsContactViewHolder<? extends AbsContactItem>> onGetFuncViewHolderClass() {
                return FuncViewHolder.class;
            }

            @Override
            public List<AbsContactItem> onGetFuncItems() {
                return FuncViewHolder.FuncItem.provide();
            }

            @Override
            public void onFuncItemClick(AbsContactItem item) {
                if (item == FuncViewHolder.FuncItem.VERIFY) {
                    SystemMessageActivity.start(getActivity());
                } else if (item == FuncViewHolder.FuncItem.ROBOT) {
                    RobotListActivity.start(getActivity());
                } else if (item == FuncViewHolder.FuncItem.NORMAL_TEAM) {
                    TeamListActivity.start(getActivity(), ItemTypes.TEAMS.NORMAL_TEAM);
                } else if (item == FuncViewHolder.FuncItem.ADVANCED_TEAM) {
                    TeamListActivity.start(getActivity(), ItemTypes.TEAMS.ADVANCED_TEAM);
                } else if (item == FuncViewHolder.FuncItem.MY_COMPUTER) {
                    SessionHelper.startP2PSession(getActivity(), DemoCache.getAccount());
                } else if (item == FuncViewHolder.FuncItem.BLACK_LIST) {
                    BlackListActivity.start(getActivity());
                }
            }
        });
    }

    @Override
    public void onCurrentTabClicked() {
        // 点击切换到当前TAB
        if (fragment != null) {
            fragment.scrollToTop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FunctionViewHolder.unRegisterUnreadNumChangedCallback();
    }
}
