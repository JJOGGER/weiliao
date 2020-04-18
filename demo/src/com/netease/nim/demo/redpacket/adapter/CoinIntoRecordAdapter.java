package com.netease.nim.demo.redpacket.adapter;

import android.support.v7.widget.RecyclerView;

import com.diamond.jogger.base.entity.IntoRecord;
import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

/**
 * Created by jogger on 2019/12/21
 * 描述：
 */
public class CoinIntoRecordAdapter extends BaseQuickAdapter<IntoRecord, BaseViewHolder> {
    public CoinIntoRecordAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.rv_coin_into_record_item, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, IntoRecord item, int position, boolean isScrolling) {
        helper.setText(R.id.tv_title, item.getTradeType());
        helper.setText(R.id.tv_amount, item.getAmount());
        helper.setText(R.id.tv_status, item.getStatus());
        helper.setText(R.id.tv_date, item.getCreateTime());
    }
}
