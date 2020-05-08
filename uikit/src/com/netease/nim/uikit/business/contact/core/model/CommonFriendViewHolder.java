package com.netease.nim.uikit.business.contact.core.model;

import android.content.Context;
import android.view.View;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.business.contact.core.item.ContactItem;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.impl.NimUIKitImpl;

public class CommonFriendViewHolder extends BaseContactViewHolder {
private Context context;
    public CommonFriendViewHolder(Context context, View view) {
        super(view);
        this.context=context;
    }

    @Override
    public void convert(AbsContactItem item) {
        if (! (item instanceof ContactItem)){
            return;
        }
        ContactItem contactItem = (ContactItem) item;
        // contact info
        final IContact contact = contactItem.getContact();
        if (contact.getContactType() == IContact.Type.Friend) {
            ((HeadImageView)getView(R.id.contacts_item_head)).loadBuddyAvatar(contact.getContactId());
        }
        setText(R.id.contacts_item_name,contact.getDisplayName());
        setOnClickListener(R.id.head_layout,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contact.getContactType() == IContact.Type.Friend) {
                    if (NimUIKitImpl.getContactEventListener() != null) {
                        NimUIKitImpl.getContactEventListener().onAvatarClick(context, contact.getContactId());
                    }
                }
            }
        });
        getView(R.id.contacts_item_desc).setVisibility(View.GONE);
    }
}
