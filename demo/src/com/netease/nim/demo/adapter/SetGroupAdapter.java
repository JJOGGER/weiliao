package com.netease.nim.demo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;

import com.diamond.jogger.base.entity.GroupData;
import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

import java.util.List;

public class SetGroupAdapter extends BaseQuickAdapter<GroupData, BaseViewHolder> {
    public SetGroupAdapter(RecyclerView recyclerView, List<GroupData> data) {
        super(recyclerView, R.layout.rv_set_group_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GroupData item, int position, boolean isScrolling) {
        helper.setText(R.id.tv_name, item.getGroupName());
        helper.setVisible(R.id.iv_check, item.isSelected());
    }
}
