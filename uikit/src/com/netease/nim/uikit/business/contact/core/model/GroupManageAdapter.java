package com.netease.nim.uikit.business.contact.core.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.diamond.jogger.base.entity.GroupData;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

import java.util.List;

public class GroupManageAdapter extends BaseQuickAdapter<GroupData, BaseViewHolder> {
    public GroupManageAdapter(RecyclerView recyclerView, List<GroupData> data) {
        super(recyclerView, R.layout.rv_group_manage_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GroupData item, int position, boolean isScrolling) {
        helper.setText(R.id.tv_name, item.getGroupName());
        helper.setOnClickListener(R.id.ibtn_remove, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = helper.getView(R.id.tv_delete);
                if (view.getVisibility() == View.VISIBLE) {
                    view.setVisibility(View.GONE);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
            }
        });
        helper.addOnClickListener(R.id.tv_delete);
    }
}
