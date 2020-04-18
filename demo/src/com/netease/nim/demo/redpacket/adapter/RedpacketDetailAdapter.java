package com.netease.nim.demo.redpacket.adapter;

import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.diamond.jogger.base.entity.RedPacketUserInfo;
import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

/**
 * Created by jogger on 2019/12/18
 * 描述：
 */
public class RedpacketDetailAdapter extends BaseQuickAdapter<RedPacketUserInfo, BaseViewHolder> {
    public RedpacketDetailAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.jrmf_rp_item_rp_detail, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, RedPacketUserInfo item, int position, boolean isScrolling) {
        helper.setText(R.id.tv_name, item.getName());
        helper.setText(R.id.tv_time, item.getReceiveTime());
        helper.setText(R.id.tv_amount, item.getAmount() / 100d + "金币");
        Glide.with(mContext)
                .load(item.getAvatar())
                .into((HeadImageView) helper.getView(R.id.iv_header));
        helper.setVisible(R.id.tv_best, item.isLuckFlag());
    }
}
