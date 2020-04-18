package com.netease.nim.uikit.business.contact.core.model;

import android.view.View;

import com.netease.nim.uikit.business.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

public abstract class BaseContactViewHolder extends BaseViewHolder {

    public BaseContactViewHolder(View view) {
        super(view);
    }

    public abstract void convert(AbsContactItem item);
}
