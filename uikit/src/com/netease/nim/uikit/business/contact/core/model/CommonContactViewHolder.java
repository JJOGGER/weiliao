package com.netease.nim.uikit.business.contact.core.model;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.contact.core.GroupManageActivity;
import com.netease.nim.uikit.business.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.business.contact.core.item.ContactGroupTopItem;
import com.netease.nim.uikit.business.contact.core.item.ContactItem;
import com.netease.nim.uikit.business.contact.core.item.ItemTypes;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;
import com.netease.nim.uikit.impl.NimUIKitImpl;

public class CommonContactViewHolder extends BaseContactViewHolder {
private Context context;
    public CommonContactViewHolder(Context context,View view) {
        super(view);
        this.context=context;
    }

    @Override
    public void convert(AbsContactItem item) {
        if (! (item instanceof ContactGroupTopItem)){
            return;
        }
        ContactGroupTopItem groupTopItem = (ContactGroupTopItem) item;
        final IContact contact = groupTopItem.getContact();
        RecyclerView rvContent = getView(R.id.rv_content);
        if (contact.getContactType() == IContact.Type.Friend) {
           setText(R.id.tv_group_name,contact.getDisplayName());
            getView(R.id.ll_group_item).setOnClickListener(v -> {
                if (rvContent.getVisibility() == View.VISIBLE) {
                    rvContent.setVisibility(View.GONE);
                   setImageResource(R.id.iv_guide,R.drawable.ic_right);
                } else {
                    rvContent.setVisibility(View.VISIBLE);
                    setImageResource(R.id.iv_guide,R.drawable.ic_down);
                }
            });
            getView(R.id.ll_group_item).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    CustomAlertDialog alertDialog = new CustomAlertDialog(context);
                    alertDialog.addItem("分组管理", new CustomAlertDialog.onSeparateItemClickListener() {
                        @Override
                        public void onClick() {
                            GroupManageActivity.startActivity(context);
                        }
                    });
                    alertDialog.show();
                    return true;
                }
            });
            setText(R.id.tv_count,groupTopItem.getContactItems().size() + "");
            rvContent.setLayoutManager(new LinearLayoutManager(context) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }

                @Override
                public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                    return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                }
            });
            ContactDataExAdapter a = new ContactDataExAdapter(rvContent, groupTopItem.getContactItems());
            rvContent.setAdapter(a);
            a.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    AbsContactItem item = (AbsContactItem) adapter.getItem(position);
                    if (item == null) {
                        return;
                    }
                    int type = item.getItemType();
                    if (type == ItemTypes.FRIEND && item instanceof ContactItem && NimUIKitImpl.getContactEventListener() != null) {
                        NimUIKitImpl.getContactEventListener().onItemClick(context, (((ContactItem) item).getContact()).getContactId());
                    }
                }
            });
            a.setOnItemLongClickListener((adapter, view, position) -> {
                AbsContactItem item1 = (AbsContactItem) adapter.getItem(position);
                if (item1 == null) {
                    return false;
                }

                if (item1 instanceof ContactItem && NimUIKitImpl.getContactEventListener() != null) {
                    NimUIKitImpl.getContactEventListener().onItemLongClick(context, (((ContactItem) item1).getContact()).getContactId());
                }

                return true;
            });
        }
    }
}
