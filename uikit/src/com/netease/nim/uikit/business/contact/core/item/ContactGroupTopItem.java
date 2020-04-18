package com.netease.nim.uikit.business.contact.core.item;

import android.text.TextUtils;

import com.netease.nim.uikit.business.contact.core.model.ContactGroupStrategy;
import com.netease.nim.uikit.business.contact.core.model.IContact;
import com.netease.nim.uikit.business.contact.core.query.TextComparator;

import java.util.List;

public class ContactGroupTopItem extends AbsContactItem implements Comparable<ContactGroupTopItem> {
    private final IContact contact;
    private List<ContactItem> mContactItems;

    public List<ContactItem> getContactItems() {
        return mContactItems;
    }

    public void setContactItems(List<ContactItem> contactItems) {
        mContactItems = contactItems;
    }

    private final int dataItemType;

    public ContactGroupTopItem(IContact contact, int type, List<ContactItem> contactItems) {
        this.contact = contact;
        this.dataItemType = type;
        mContactItems = contactItems;
    }

    public IContact getContact() {
        return contact;
    }

    @Override
    public int getItemType() {
        return dataItemType;
    }

    @Override
    public int compareTo(ContactGroupTopItem item) {
        // TYPE
        int compare = compareType(item);
        if (compare != 0) {
            return compare;
        } else {
            return TextComparator.compareIgnoreCase(getCompare(), item.getCompare());
        }
    }

    @Override
    public String belongsGroup() {
        IContact contact = getContact();
        if (contact == null) {
            return ContactGroupStrategy.GROUP_NULL;
        }

        String group = TextComparator.getLeadingUp(getCompare());
        return !TextUtils.isEmpty(group) ? group : ContactGroupStrategy.GROUP_SHARP;
    }

    private String getCompare() {
        IContact contact = getContact();
        return contact != null ? contact.getDisplayName() : null;
    }
}
