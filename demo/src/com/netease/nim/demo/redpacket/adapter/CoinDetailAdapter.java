package com.netease.nim.demo.redpacket.adapter;

import android.support.v7.widget.RecyclerView;

import com.diamond.jogger.base.entity.CoinDetailResponse;
import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

/**
 * Created by jogger on 2020/1/3
 * 描述：
 */
public class CoinDetailAdapter extends BaseQuickAdapter<CoinDetailResponse.CoinDetail, BaseViewHolder> {
    public CoinDetailAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.rv_coin_detail_record_item, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, CoinDetailResponse.CoinDetail item, int position, boolean isScrolling) {
        helper.setText(R.id.tv_title, item.getDesc());
        helper.setText(R.id.tv_date, item.getCreateTime());
    }
}
