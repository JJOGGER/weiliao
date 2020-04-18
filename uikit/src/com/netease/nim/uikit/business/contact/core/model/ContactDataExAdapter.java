package com.netease.nim.uikit.business.contact.core.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.business.contact.core.item.ContactItem;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.List;

public class ContactDataExAdapter extends BaseQuickAdapter<ContactItem, BaseViewHolder> {
    public ContactDataExAdapter(RecyclerView recyclerView, List<ContactItem> data) {
        super(recyclerView, R.layout.nim_contacts_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ContactItem item, int position, boolean isScrolling) {
        // contact info
        final IContact contact = item.getContact();
        if (contact.getContactType() == IContact.Type.Friend) {
            ((HeadImageView)helper.getView(R.id.contacts_item_head)).loadBuddyAvatar(contact.getContactId());
        }
        helper.setText(R.id.contacts_item_name,contact.getDisplayName());
        helper.setOnClickListener(R.id.head_layout,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contact.getContactType() == IContact.Type.Friend) {
                    if (NimUIKitImpl.getContactEventListener() != null) {
                        NimUIKitImpl.getContactEventListener().onAvatarClick(mContext, item.getContact().getContactId());
                    }
                }
            }
        });
        helper.getView(R.id.contacts_item_desc).setVisibility(View.GONE);
    }
}
