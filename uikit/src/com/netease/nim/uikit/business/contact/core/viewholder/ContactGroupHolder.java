package com.netease.nim.uikit.business.contact.core.viewholder;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.contact.core.GroupManageActivity;
import com.netease.nim.uikit.business.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.business.contact.core.item.ContactGroupTopItem;
import com.netease.nim.uikit.business.contact.core.item.ContactItem;
import com.netease.nim.uikit.business.contact.core.item.ItemTypes;
import com.netease.nim.uikit.business.contact.core.model.ContactDataAdapter;
import com.netease.nim.uikit.business.contact.core.model.ContactDataExAdapter;
import com.netease.nim.uikit.business.contact.core.model.IContact;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.impl.NimUIKitImpl;

public class ContactGroupHolder extends AbsContactViewHolder<ContactGroupTopItem> {
    RecyclerView rvContent;
    TextView tvName, tvCount;
    ImageView ivGuide;
    View groupItem;

    @Override
    public void refresh(ContactDataAdapter adapter, int position, ContactGroupTopItem item) {
        final IContact contact = item.getContact();
        if (contact.getContactType() == IContact.Type.Friend) {
            tvName.setText(contact.getDisplayName());
            groupItem.setOnClickListener(v -> {
                if (rvContent.getVisibility() == View.VISIBLE) {
                    rvContent.setVisibility(View.GONE);
                    ivGuide.setImageResource(R.drawable.ic_right);
                } else {
                    rvContent.setVisibility(View.VISIBLE);
                    ivGuide.setImageResource(R.drawable.ic_down);
                }
            });
            groupItem.setOnLongClickListener(new View.OnLongClickListener() {
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
            tvCount.setText(item.getContactItems().size() + "");
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
            ContactDataExAdapter a = new ContactDataExAdapter(rvContent, item.getContactItems());
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
            a.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                    AbsContactItem item = (AbsContactItem) adapter.getItem(position);
                    if (item == null) {
                        return false;
                    }

                    if (item instanceof ContactItem && NimUIKitImpl.getContactEventListener() != null) {
                        NimUIKitImpl.getContactEventListener().onItemLongClick(context, (((ContactItem) item).getContact()).getContactId());
                    }

                    return true;
                }
            });
        }
    }

    @Override
    public View inflate(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.nim_contacts_grou_top_item, null);
        rvContent = view.findViewById(R.id.rv_content);
        groupItem = view.findViewById(R.id.ll_group_item);
        tvName = view.findViewById(R.id.tv_group_name);
        ivGuide = view.findViewById(R.id.iv_guide);
        tvCount = view.findViewById(R.id.tv_count);
        return view;
    }
}
