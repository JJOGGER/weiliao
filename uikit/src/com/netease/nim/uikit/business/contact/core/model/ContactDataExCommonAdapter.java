package com.netease.nim.uikit.business.contact.core.model;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.business.contact.core.item.ContactGroupTopItem;
import com.netease.nim.uikit.business.contact.core.item.ContactItem;
import com.netease.nim.uikit.business.contact.core.item.ContactItemFilter;
import com.netease.nim.uikit.business.contact.core.item.GroupTopItem;
import com.netease.nim.uikit.business.contact.core.item.ItemTypes;
import com.netease.nim.uikit.business.contact.core.model.ContactDataTask.Host;
import com.netease.nim.uikit.business.contact.core.query.IContactDataProvider;
import com.netease.nim.uikit.business.contact.core.query.TextQuery;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemQuickAdapter;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.impl.cache.UIKitLogTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;


/**
 * 通讯录数据适配器
 * <p/>
 * Created by huangjun on 2015/2/10.
 */
public class ContactDataExCommonAdapter extends BaseMultiItemQuickAdapter<AbsContactItem, BaseContactViewHolder> {


//    private final SparseArray<Class<? extends AbsContactViewHolder<? extends AbsContactItem>>> viewHolderMap;

    private final ContactGroupStrategy groupStrategy;
    private boolean isSort = false;

    private final IContactDataProvider dataProvider;

//    private List<AbsContactItem> datas=new ArrayList<>();

    private final HashMap<String, Integer> indexes = new HashMap<>();

    private ContactItemFilter filter;
    //用于记录当前班级是隐藏还是显示
    private List<AbsContactItem> mContactItems = new ArrayList<>();

    public ContactDataExCommonAdapter(RecyclerView recyclerView, ContactGroupStrategy groupStrategy, IContactDataProvider dataProvider) {
        super(recyclerView, null);
        this.dataProvider = dataProvider;
        this.groupStrategy = groupStrategy;
        addItemType(ItemTypes.FUNC, R.layout.func_contacts_item);
        addItemType(ItemTypes.GROUP_TOP, R.layout.nim_contacts_grou_top_item);
        addItemType(ItemTypes.FRIEND, R.layout.nim_contacts_item);
    }


    @Override
    protected BaseContactViewHolder createBaseViewHolder(ViewGroup parent, int layoutResId) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        if (layoutResId == R.layout.func_contacts_item) {
            return new FunctionViewHolder(layout);
        } else if (layoutResId == R.layout.nim_contacts_grou_top_item) {
            return new CommonContactViewHolder(this, parent.getContext(), layout);
        } else {
            return new CommonFriendViewHolder(parent.getContext(), layout);
        }
    }

    @Override
    protected void convert(BaseContactViewHolder baseHolder, AbsContactItem item, int position, boolean isScrolling) {
        baseHolder.convert(item);
    }

    public final void setFilter(ContactItemFilter filter) {
        this.filter = filter;
    }


    public final TextQuery getQuery() {
        return null;
    }

    private void updateData(AbsContactDataList datas) {
        mContactItems.clear();
        mData.clear();
        List<AbsContactItem> items = datas.getItems();
        for (int i = 0; i < items.size(); i++) {
            AbsContactItem absContactItem = items.get(i);
            if (items.get(i).getItemType() == ItemTypes.FRIEND) {
                if (!(absContactItem instanceof ContactGroupTopItem)) {
                    continue;
                }
                ContactGroupTopItem groupTopItem = (ContactGroupTopItem) absContactItem;
                List<ContactItem> contactItems = groupTopItem.getContactItems();
                int size = contactItems == null ? 0 : contactItems.size();
                mData.add(new GroupTopItem(groupTopItem.getContact().getDisplayName(), i, size));
                if (size == 0)
                    continue;
                for (int j = 0; j < contactItems.size(); j++) {
                    contactItems.get(j).setPosition(i);
                }
                mData.addAll(contactItems);
            } else {
                mData.add(absContactItem);
            }
        }
        mContactItems.addAll(mData);
//        updateIndexes(datas.getIndexes());
        notifyDataSetChanged();
    }

    public void hideFriends(int position, GroupTopItem groupTopItem, boolean isHide) {
        mData.clear();
        mData.addAll(mContactItems);
        ListIterator<AbsContactItem> listIterator = mData.listIterator();
        while (listIterator.hasNext()) {
            AbsContactItem absContactItem =listIterator.next();
            if (absContactItem.equals(groupTopItem)) {
                GroupTopItem item = (GroupTopItem) absContactItem;
                item.setHide(isHide);
            }
            if (absContactItem.getItemType() == ItemTypes.FRIEND) {
                if (!(absContactItem instanceof ContactItem)) {
                    continue;
                }
                ContactItem contactItem = (ContactItem) absContactItem;
                if (contactItem.getPosition() == groupTopItem.getPosition()) {
                    contactItem.setHide(isHide);
                    if (isHide) {
                        listIterator.remove();
                    }
                }
            }
        }
        notifyDataSetChanged();
    }


    public final void query(String query) {
        startTask(new TextQuery(query), true);
    }

    public final boolean load(boolean reload) {
        if (!reload && !mData.isEmpty()) {
            return false;
        }

        LogUtil.i(UIKitLogTag.CONTACT, "contact load data");

        startTask(null, false);

        return true;
    }

    public final void query(TextQuery query) {
        startTask(query, true);
    }

    private final List<Task> tasks = new ArrayList<>();

    /**
     * 启动搜索任务
     *
     * @param query 要搜索的信息，填null表示查询所有数据
     * @param abort 是否终止：例如搜索的时候，第一个搜索词还未搜索完成，第二个搜索词已生成，那么取消之前的搜索任务
     */
    private void startTask(TextQuery query, boolean abort) {
        if (abort) {
            for (Task task : tasks) {
                task.cancel(false); // 设为true有风险！
            }
        }

        Task task = new Task(new ContactDataTask(query, dataProvider, filter, isSort) {
            @Override
            protected void onPreProvide(AbsContactDataList datas) {
                List<? extends AbsContactItem> itemsND = onNonDataItems();

                if (itemsND != null) {
                    for (AbsContactItem item : itemsND) {
                        datas.add(item, isSort);
                    }
                }
            }
        });

        tasks.add(task);

        task.execute();
    }

    private void onTaskFinish(Task task) {
        tasks.remove(task);
    }

    @Override
    protected int getViewType(AbsContactItem item) {
        int type = item.getItemType();
        return type;
//        return viewHolderMap.indexOfKey(type);
    }

    @Override
    protected String getItemKey(AbsContactItem item) {
        return null;
    }

    /**
     * 搜索/查询数据异步任务
     */

    private class Task extends AsyncTask<Void, Object, Void> implements Host {
        final ContactDataTask task;

        Task(ContactDataTask task) {
            task.setHost(this);

            this.task = task;
        }

        @Override
        public void onData(ContactDataTask task, AbsContactDataList datas, boolean all) {
            publishProgress(datas, all);
        }

        @Override
        public boolean isCancelled(ContactDataTask task) {
            return isCancelled();
        }

        @Override
        protected void onPreExecute() {
            onPreReady();
        }

        @Override
        protected Void doInBackground(Void... params) {
            task.run(new ContactDataList(groupStrategy, isSort));

            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            AbsContactDataList datas = (AbsContactDataList) values[0];
            boolean all = (Boolean) values[1];

            onPostLoad(datas.isEmpty(), datas.getQueryText(), all);

            updateData(datas);
        }

        @Override
        protected void onPostExecute(Void result) {
            onTaskFinish(this);
        }

        @Override
        protected void onCancelled() {
            onTaskFinish(this);
        }

    }


    /**
     * 数据未准备
     */
    protected void onPreReady() {
    }

    /**
     * 数据加载完成
     */
    protected void onPostLoad(boolean empty, String query, boolean all) {
    }

    /**
     * 加载完成后，加入非数据项
     *
     * @return
     */
    protected List<? extends AbsContactItem> onNonDataItems() {
        return null;
    }


    private Map<String, Integer> getIndexes() {
        return this.indexes;
    }

    private void updateIndexes(Map<String, Integer> indexes) {
        // CLEAR
        this.indexes.clear();
        // SET
        this.indexes.putAll(indexes);
    }

}
