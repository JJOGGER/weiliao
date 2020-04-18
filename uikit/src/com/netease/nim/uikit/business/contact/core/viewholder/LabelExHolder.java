package com.netease.nim.uikit.business.contact.core.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.contact.core.item.LabelItem;
import com.netease.nim.uikit.business.contact.core.model.ContactDataAdapter;

public class LabelExHolder extends AbsContactViewHolder<LabelItem> {

    private TextView name;

    @Override
    public void refresh(ContactDataAdapter contactAdapter, int position, LabelItem item) {
    }

    @Override
    public View inflate(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.nim_contacts_abc_ex_item, null);
        this.name = view.findViewById(R.id.tv_nickname);
        return view;
    }

}
