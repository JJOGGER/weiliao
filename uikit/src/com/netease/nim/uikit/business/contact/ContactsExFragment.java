package com.netease.nim.uikit.business.contact;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;

import com.diamond.jogger.base.entity.GroupData;
import com.diamond.jogger.base.event.GroupChangeEvent;
import com.diamond.jogger.base.http.basic.callback.RequestMultiplyCallback;
import com.diamond.jogger.base.http.basic.exception.base.BaseException;
import com.diamond.jogger.base.http.datasource.UserDataSource;
import com.diamond.jogger.base.http.datasource.base.IUserDataSource;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.contact.ContactChangedObserver;
import com.netease.nim.uikit.api.model.contact.ContactsCustomization;
import com.netease.nim.uikit.api.model.main.LoginSyncDataStatusObserver;
import com.netease.nim.uikit.api.model.main.OnlineStateChangeObserver;
import com.netease.nim.uikit.api.model.user.UserInfoObserver;
import com.netease.nim.uikit.business.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.business.contact.core.item.ContactItem;
import com.netease.nim.uikit.business.contact.core.item.ItemTypes;
import com.netease.nim.uikit.business.contact.core.model.ContactDataCommonAdapter;
import com.netease.nim.uikit.business.contact.core.model.ContactDataExCommonAdapter;
import com.netease.nim.uikit.business.contact.core.model.ContactGroupStrategy;
import com.netease.nim.uikit.business.contact.core.provider.ContactExDataProvider;
import com.netease.nim.uikit.business.contact.core.query.IContactDataProvider;
import com.netease.nim.uikit.business.contact.core.viewholder.AbsContactViewHolder;
import com.netease.nim.uikit.business.contact.core.viewholder.ContactGroupHolder;
import com.netease.nim.uikit.business.contact.core.viewholder.LabelExHolder;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nim.uikit.impl.cache.UIKitLogTag;
import com.netease.nimlib.sdk.Observer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * 通讯录Fragment
 * <p/>
 * Created by huangjun on 2015/9/7.
 */
public class ContactsExFragment extends TFragment {

    private ContactDataExCommonAdapter adapter;

    //    private ListView listView;
    private RecyclerView rvMain;
    private TextView countText;

//    private LivIndex litterIdx;

    private View loadingFrame;

    private ContactsCustomization customization;

    private ReloadFrequencyControl reloadControl = new ReloadFrequencyControl();
    private IUserDataSource mUserDataSource;

    public void setContactsCustomization(ContactsCustomization customization) {
        this.customization = customization;
    }

    private static final class ContactsGroupStrategy extends ContactGroupStrategy {
        public ContactsGroupStrategy() {
//            add(ContactGroupStrategy.GROUP_NULL, -1, "");
//            addABC(0);
        }
    }

    /**
     * ***************************************** 生命周期 *****************************************
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.nim_contacts_ex, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rvMain = findView(R.id.rv_main);
        rvMain.setLayoutManager(new LinearLayoutManager(getContext()));
        // 界面初始化
        initAdapter();
        findViews();
//        buildLitterIdx(getView());

        // 注册观察者
        registerObserver(true);
        registerOnlineStateChangeListener(true);
        mUserDataSource = new UserDataSource();
        mUserDataSource.getGroupList(NimUIKit.getServerAccount(), new RequestMultiplyCallback<List<GroupData>>() {
            @Override
            public void onFail(BaseException e) {
                ToastHelper.showToast(getContext(), e.getMessage());
                reload(false);
            }

            @Override
            public void onSuccess(List<GroupData> groupData) {
                // 加载本地数据
                reload(false);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        registerObserver(false);
        registerOnlineStateChangeListener(false);
    }

    private void initAdapter() {
        IContactDataProvider dataProvider = new ContactExDataProvider(ItemTypes.FRIEND);

        adapter = new ContactDataExCommonAdapter(rvMain,new ContactsGroupStrategy(), dataProvider) {
            @Override
            protected List<AbsContactItem> onNonDataItems() {
                if (customization != null) {
                    return customization.onGetFuncItems();
                }

                return new ArrayList<>();
            }

            @Override
            protected void onPreReady() {
                loadingFrame.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostLoad(boolean empty, String queryText, boolean all) {
                loadingFrame.setVisibility(View.GONE);
                int userCount = NimUIKit.getContactProvider().getMyFriendsCount();
                countText.setText("共有好友" + userCount + "名");

                onReloadCompleted();
            }
        };

//        adapter.addViewHolder(ItemTypes.LABEL, LabelExHolder.class);
//        if (customization != null) {
//            try {
//                AbsContactViewHolder<AbsContactItem> holder = (AbsContactViewHolder<AbsContactItem>) customization.onGetFuncViewHolderClass().newInstance();
//                holder.create(getContext());
//                adapter.addHeaderView(holder.getView());
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (java.lang.InstantiationException e) {
//                e.printStackTrace();
//            }
//            adapter.addViewHolder(ItemTypes.FUNC, customization.onGetFuncViewHolderClass());
//        }
//        adapter.addViewHolder(ItemTypes.GROUP_TOP, ContactGroupHolder.class);
//        adapter.addViewHolder(ItemTypes.FRIEND, OnlineStateContactHolder.class);
    }

    private void findViews() {
        // loading
        loadingFrame = findView(R.id.contact_loading_frame);

        // count
        View countLayout = View.inflate(getView().getContext(), R.layout.nim_contacts_count_item, null);
        countLayout.setClickable(false);
        countText = countLayout.findViewById(R.id.contactCountText);

        // ListView
//        listView = findView(R.id.contact_list_view);
        adapter.addFooterView(countLayout); // 注意：addFooter要放在setAdapter之前，否则旧版本手机可能会add不上
        // 设置缓存
        rvMain.setItemViewCacheSize(200);
// 设置部分或固定的尺寸
        rvMain.setHasFixedSize(true);
// 滚动事件派发
        rvMain.setNestedScrollingEnabled(false);
        rvMain.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        rvMain.setAdapter(adapter);
//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//            }
//        });

        ContactItemClickListener listener = new ContactItemClickListener();
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AbsContactItem item = (AbsContactItem) adapter.getItem(position);
                if (item == null) {
                    return;
                }

                int type = item.getItemType();

                if (type == ItemTypes.FUNC && customization != null) {
                    customization.onFuncItemClick(item);
                    return;
                }

                if (type == ItemTypes.FRIEND && item instanceof ContactItem && NimUIKitImpl.getContactEventListener() != null) {
                    NimUIKitImpl.getContactEventListener().onItemClick(getActivity(), (((ContactItem) item).getContact()).getContactId());
                }
            }
        });
//        listView.setOnItemClickListener(listener);
//        listView.setOnItemLongClickListener(listener);

        // ios style
//        OverScrollDecoratorHelper.setUpOverScroll(rvMain,RecyclerView.VISIBLE);
    }

    private void buildLitterIdx(View view) {
//        LetterIndexView livIndex = view.findViewById(R.id.liv_index);
//        livIndex.setNormalColor(getResources().getColor(R.color.contacts_letters_color));
//        ImageView imgBackLetter = view.findViewById(R.id.img_hit_letter);
//        TextView litterHit = view.findViewById(R.id.tv_hit_letter);
//        litterIdx = adapter.createLivIndex(listView, livIndex, litterHit, imgBackLetter);

//        litterIdx.show();
    }

    private final class ContactItemClickListener implements OnItemClickListener, OnItemLongClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            AbsContactItem item = (AbsContactItem) adapter.getItem(position);
            if (item == null) {
                return;
            }

            int type = item.getItemType();

            if (type == ItemTypes.FUNC && customization != null) {
                customization.onFuncItemClick(item);
                return;
            }

            if (type == ItemTypes.FRIEND && item instanceof ContactItem && NimUIKitImpl.getContactEventListener() != null) {
                NimUIKitImpl.getContactEventListener().onItemClick(getActivity(), (((ContactItem) item).getContact()).getContactId());
            }
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {
            AbsContactItem item = (AbsContactItem) adapter.getItem(position);
            if (item == null) {
                return false;
            }

            if (item instanceof ContactItem && NimUIKitImpl.getContactEventListener() != null) {
                NimUIKitImpl.getContactEventListener().onItemLongClick(getActivity(), (((ContactItem) item).getContact()).getContactId());
            }

            return true;
        }
    }

    public void scrollToTop() {
        if (rvMain != null) {
            rvMain.scrollToPosition(0);
//            int top = listView.getFirstVisiblePosition();
//            int bottom = listView.getLastVisiblePosition();
//            if (top >= (bottom - top)) {
//                listView.setSelection(bottom - top);
//                listView.smoothScrollToPosition(0);
//            } else {
//                listView.smoothScrollToPosition(0);
//            }
        }
    }

    /**
     * *********************************** 通讯录加载控制 *******************************
     */

    /**
     * 加载通讯录数据并刷新
     *
     * @param reload true则重新加载数据；false则判断当前数据源是否空，若空则重新加载，不空则不加载
     */
    private void reload(boolean reload) {
        if (!reloadControl.canDoReload(reload)) {
            return;
        }

        if (adapter == null) {
            if (getActivity() == null) {
                return;
            }

            initAdapter();
        }

        // 开始加载
        if (!adapter.load(reload)) {
            // 如果不需要加载，则直接当完成处理
            onReloadCompleted();
        }
    }

    private void onReloadCompleted() {
        if (reloadControl.continueDoReloadWhenCompleted()) {
            // 计划下次加载，稍有延迟
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean reloadParam = reloadControl.getReloadParam();
                    Log.i(UIKitLogTag.CONTACT, "continue reload " + reloadParam);
                    reloadControl.resetStatus();
                    reload(reloadParam);
                }
            }, 50);
        } else {
            // 本次加载完成
            reloadControl.resetStatus();
        }

        LogUtil.i(UIKitLogTag.CONTACT, "contact load completed");
    }

    /**
     * 通讯录加载频率控制
     */
    class ReloadFrequencyControl {
        boolean isReloading = false;
        boolean needReload = false;
        boolean reloadParam = false;

        boolean canDoReload(boolean param) {
            if (isReloading) {
                // 正在加载，那么计划加载完后重载
                needReload = true;
                if (param) {
                    // 如果加载过程中又有多次reload请求，多次参数只要有true，那么下次加载就是reload(true);
                    reloadParam = true;
                }
                LogUtil.i(UIKitLogTag.CONTACT, "pending reload task");

                return false;
            } else {
                // 如果当前空闲，那么立即开始加载
                isReloading = true;
                return true;
            }
        }

        boolean continueDoReloadWhenCompleted() {
            return needReload;
        }

        void resetStatus() {
            isReloading = false;
            needReload = false;
            reloadParam = false;
        }

        boolean getReloadParam() {
            return reloadParam;
        }
    }

    /**
     * *********************************** 用户资料、好友关系变更、登录数据同步完成观察者 *******************************
     */

    private void registerObserver(boolean register) {
        NimUIKit.getUserInfoObservable().registerObserver(userInfoObserver, register);
        NimUIKit.getContactChangedObservable().registerObserver(friendDataChangedObserver, register);
        LoginSyncDataStatusObserver.getInstance().observeSyncDataCompletedEvent(loginSyncCompletedObserver);
        if (register) {
            EventBus.getDefault().register(this);
        } else {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupChangeEvent(GroupChangeEvent event) {
        // 加载本地数据
        reload(true);
    }

    ContactChangedObserver friendDataChangedObserver = new ContactChangedObserver() {
        @Override
        public void onAddedOrUpdatedFriends(List<String> accounts) {
            reloadWhenDataChanged(accounts, "onAddedOrUpdatedFriends", true);
        }

        @Override
        public void onDeletedFriends(List<String> accounts) {
            reloadWhenDataChanged(accounts, "onDeletedFriends", true);
        }

        @Override
        public void onAddUserToBlackList(List<String> accounts) {
            reloadWhenDataChanged(accounts, "onAddUserToBlackList", true);
        }

        @Override
        public void onRemoveUserFromBlackList(List<String> accounts) {
            reloadWhenDataChanged(accounts, "onRemoveUserFromBlackList", true);
        }
    };

    private UserInfoObserver userInfoObserver = new UserInfoObserver() {
        @Override
        public void onUserInfoChanged(List<String> accounts) {
            reloadWhenDataChanged(accounts, "onUserInfoChanged", true, false); // 非好友资料变更，不用刷新界面
        }
    };

    private Observer<Void> loginSyncCompletedObserver = new Observer<Void>() {
        @Override
        public void onEvent(Void aVoid) {
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    reloadWhenDataChanged(null, "onLoginSyncCompleted", false);
                }
            }, 50);
        }
    };

    private void reloadWhenDataChanged(List<String> accounts, String reason, boolean reload) {
        reloadWhenDataChanged(accounts, reason, reload, true);
    }

    private void reloadWhenDataChanged(List<String> accounts, String reason, boolean reload, boolean force) {
        if (accounts == null || accounts.isEmpty()) {
            return;
        }

        boolean needReload = false;
        if (!force) {
            // 非force：与通讯录无关的（非好友）变更通知，去掉
            for (String account : accounts) {
                if (NimUIKit.getContactProvider().isMyFriend(account)) {
                    needReload = true;
                    break;
                }
            }
        } else {
            needReload = true;
        }

        if (!needReload) {
            Log.d(UIKitLogTag.CONTACT, "no need to reload contact");
            return;
        }

        // log
        StringBuilder sb = new StringBuilder();
        sb.append("ContactFragment received data changed as [" + reason + "] : ");
        if (accounts != null && !accounts.isEmpty()) {
            for (String account : accounts) {
                sb.append(account);
                sb.append(" ");
            }
            sb.append(", changed size=" + accounts.size());
        }
        Log.i(UIKitLogTag.CONTACT, sb.toString());

        // reload
        reload(reload);
    }

    /**
     * *********************************** 在线状态 *******************************
     */

    OnlineStateChangeObserver onlineStateChangeObserver = new OnlineStateChangeObserver() {
        @Override
        public void onlineStateChange(Set<String> accounts) {
            // 更新
            adapter.notifyDataSetChanged();
        }
    };

    private void registerOnlineStateChangeListener(boolean register) {
        if (!NimUIKitImpl.enableOnlineState()) {
            return;
        }
        NimUIKitImpl.getOnlineStateChangeObservable().registerOnlineStateChangeListeners(onlineStateChangeObserver, register);
    }
}
