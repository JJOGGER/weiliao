package com.netease.nim.demo.redpacket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.diamond.jogger.base.entity.RedPacketConstant;
import com.diamond.jogger.base.entity.RedPacketStatusResponse;
import com.diamond.jogger.base.entity.RedPacketUserInfo;
import com.diamond.jogger.base.utils.BigDecimalUtils;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.main.model.Extras;
import com.netease.nim.demo.redpacket.adapter.RedpacketDetailAdapter;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

public class ChangePointRedPacketDetailsActivity extends UI implements View.OnClickListener {
    HeadImageView ivAvatar;
    TextView tvName;
    TextView tvBlessing;
    TextView tvAmount;
    TextView tvAlAccount;
    RecyclerView rvContent;
    private TextView tvbless;
    private RedpacketDetailAdapter mAdapter;
    private RedPacketStatusResponse mRedPacketStatusResponse;
    private TextView tvRecAmount;
    private TextView tvAmountMsg;
    private TextView tvRpNum;
    private ImageView ivIsLuck;

    public static void navTo(Context context, RedPacketStatusResponse response) {
        Intent intent = new Intent(context, ChangePointRedPacketDetailsActivity.class);
        intent.putExtra(Extras.RED_PACKET_DETAIL, response);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_redpacket);
        mRedPacketStatusResponse = (RedPacketStatusResponse) getIntent().getSerializableExtra(Extras.RED_PACKET_DETAIL);
        if (mRedPacketStatusResponse == null) return;
        rvContent = findView(R.id.rv_content);
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        View header = LayoutInflater.from(this).inflate(R.layout.jrmf_rp_header_rp_detail, null);
        tvName = header.findViewById(R.id.tv_username);
        ivIsLuck = header.findViewById(R.id.iv_is_luck);
        tvbless = header.findViewById(R.id.tv_bless);
        ivAvatar = header.findViewById(R.id.iv_avatar);
        tvRecAmount = header.findViewById(R.id.tv_rec_amount);
        tvAmountMsg = header.findViewById(R.id.tv_amount_msg);
        tvRpNum = header.findViewById(R.id.tv_rp_num);
        header.findViewById(R.id.ibtn_back).setOnClickListener(v -> {
            finish();
        });
        mAdapter = new RedpacketDetailAdapter(rvContent);
        mAdapter.addHeaderView(header);
        rvContent.setAdapter(mAdapter);
        ivAvatar.loadAvatar(mRedPacketStatusResponse.getAvatar());
        tvName.setText(mRedPacketStatusResponse.getName());
        if (!RedPacketConstant.RedStatus.STATUS_ACCEPT_OVER.equals(mRedPacketStatusResponse.getRedStatus()) &&
                !RedPacketConstant.RedStatus.STATUS_ACCEPT_BACK.equals(mRedPacketStatusResponse.getRedStatus())) {
            tvRecAmount.setVisibility(View.VISIBLE);
            tvAmountMsg.setVisibility(View.VISIBLE);
            for (int i = 0; i < mRedPacketStatusResponse.getAccountList().size(); i++) {
                RedPacketUserInfo redPacketUserInfo = mRedPacketStatusResponse.getAccountList().get(i);
                if (redPacketUserInfo.getAccount().equals(DemoCache.getAccount())) {
                    tvRecAmount.setText(redPacketUserInfo.getAmountYuan());
                    break;
                }
            }
        }
        ivIsLuck.setVisibility(RedPacketConstant.RedType.TYPE_NORMAL.equals(mRedPacketStatusResponse.getRedType()) ? View.GONE : View.VISIBLE);
        if (RedPacketConstant.RedStatus.STATUS_ACCEPT_OVER.equals(mRedPacketStatusResponse.getRedStatus())) {
            tvRpNum.setText(mRedPacketStatusResponse.getTotalCount() + "个红包共" + mRedPacketStatusResponse.getTotalAmount() + "，" + mRedPacketStatusResponse.getFinishTime() + "被抢光");
        } else {
            String sub = BigDecimalUtils.sub(mRedPacketStatusResponse.getTotalAmount().replace("金币",""), mRedPacketStatusResponse.getRetainAmount());
            tvRpNum.setText("已领取" + (mRedPacketStatusResponse.getTotalCount() - mRedPacketStatusResponse.getRetainCount()) + "/"
                    + mRedPacketStatusResponse.getTotalCount() + "，共" +  sub + "/" + mRedPacketStatusResponse.getTotalAmount());
        }
        mAdapter.setNewData(mRedPacketStatusResponse.getAccountList());
    }

    @Override
    public void onClick(View v) {

    }
}
