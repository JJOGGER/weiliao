package com.netease.nim.uikit.business.contact.core.model;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.business.contact.core.item.ContactItemFilter;
import com.netease.nim.uikit.business.contact.core.item.ItemTypes;
import com.netease.nim.uikit.business.contact.core.model.ContactDataTask.Host;
import com.netease.nim.uikit.business.contact.core.query.IContactDataProvider;
import com.netease.nim.uikit.business.contact.core.query.TextQuery;
import com.netease.nim.uikit.business.contact.core.viewholder.AbsContactViewHolder;
import com.netease.nim.uikit.common.ui.liv.LetterIndexView;
import com.netease.nim.uikit.common.ui.liv.LivIndex;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemQuickAdapter;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.impl.cache.UIKitLogTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 通讯录数据适配器
 * <p/>
 * Created by huangjun on 2015/2/10.
 */
public class ContactDataCommonAdapter extends BaseMultiItemQuickAdapter<AbsContactItem, BaseContactViewHolder> {


//    private final SparseArray<Class<? extends AbsContactViewHolder<? extends AbsContactItem>>> viewHolderMap;

    private final ContactGroupStrategy groupStrategy;
    private boolean isSort = false;

    private final IContactDataProvider dataProvider;

//    private List<AbsContactItem> datas=new ArrayList<>();

    private final HashMap<String, Integer> indexes = new HashMap<>();

    private ContactItemFilter filter;

    private ContactItemFilter disableFilter;

    public ContactDataCommonAdapter(RecyclerView recyclerView, ContactGroupStrategy groupStrategy, IContactDataProvider dataProvider) {
        super(recyclerView, null);
        this.dataProvider = dataProvider;
        this.groupStrategy = groupStrategy;
        addItemType(ItemTypes.FUNC, R.layout.func_contacts_item);
        addItemType(ItemTypes.GROUP_TOP, R.layout.nim_contacts_grou_top_item);
    }

    @Override
    protected BaseContactViewHolder createBaseViewHolder(ViewGroup parent, int layoutResId) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        if (layoutResId == R.layout.func_contacts_item) {
            return new FunctionViewHolder(layout);
        } else {
            return new CommonContactViewHolder(parent.getContext(), layout);
        }
    }

    @Override
    protected void convert(BaseContactViewHolder baseHolder, AbsContactItem item, int position, boolean isScrolling) {
        baseHolder.convert(item);
    }

    //    public ContactDataCommonAdapter(RecyclerView recyclerView,  ContactGroupStrategy groupStrategy, IContactDataProvider dataProvider) {
//        super(recyclerView, R.layout.nim_rv_contact_ex_item, null);
//        this.groupStrategy = groupStrategy;
//        this.dataProvider = dataProvider;
//        this.viewHolderMap = new SparseArray<>(6);
//    }
//    public ContactDataCommonAdapter(RecyclerView recyclerView,ContactGroupStrategy groupStrategy, IContactDataProvider dataProvider, boolean isSort) {
//        super(recyclerView, R.layout.nim_rv_contact_ex_item, null);
//        this.groupStrategy = groupStrategy;
//        this.dataProvider = dataProvider;
//        this.viewHolderMap = new SparseArray<>(6);
//        this.isSort=isSort;
//    }
    //    public ContactDataCommonAdapter(Context context, ContactGroupStrategy groupStrategy, IContactDataProvider dataProvider) {
//        this.context = context;
//        this.groupStrategy = groupStrategy;
//        this.dataProvider = dataProvider;
//        this.viewHolderMap = new SparseArray<>(6);
//    }
//    public ContactDataCommonAdapter(Context context, ContactGroupStrategy groupStrategy, IContactDataProvider dataProvider, boolean isSort) {
//        this.context = context;
//        this.groupStrategy = groupStrategy;
//        this.isSort=isSort;
//        this.dataProvider = dataProvider;
//        this.viewHolderMap = new SparseArray<>(6);
//    }
    public void addViewHolder(int itemDataType, Class<? extends AbsContactViewHolder<? extends AbsContactItem>> viewHolder) {
//        this.viewHolderMap.put(itemDataType, viewHolder);
    }

    public final void setFilter(ContactItemFilter filter) {
        this.filter = filter;
    }

    public final void setDisableFilter(ContactItemFilter disableFilter) {
        this.disableFilter = disableFilter;
    }

    public final LivIndex createLivIndex(ListView lv, LetterIndexView liv, TextView tvHit, ImageView ivBk) {
        return new LivIndex(lv, liv, tvHit, ivBk, getIndexes());
    }


    public final TextQuery getQuery() {
        return null;
    }

    private void updateData(AbsContactDataList datas) {
        mData = datas.getItems();

//        updateIndexes(datas.getIndexes());
        notifyDataSetChanged();
    }


    public boolean isEnabled(int position) {
        if (disableFilter != null) {
            return !disableFilter.filter((AbsContactItem) getItem(position));
        }

        return true;
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
