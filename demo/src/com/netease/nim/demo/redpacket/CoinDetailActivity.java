package com.netease.nim.demo.redpacket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.diamond.jogger.base.entity.CoinDetailResponse;
import com.diamond.jogger.base.http.basic.callback.RequestMultiplyCallback;
import com.diamond.jogger.base.http.basic.exception.base.BaseException;
import com.diamond.jogger.base.http.datasource.CoinDataSource;
import com.diamond.jogger.base.http.datasource.base.ICoinDataSource;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.redpacket.adapter.CoinDetailAdapter;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;

public class CoinDetailActivity extends UI implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView rvContent;
    private SwipeRefreshLayout srlRefresh;
    private ICoinDataSource mCoinDataSource;
    private int mPage = 1;
    private CoinDetailAdapter mAdapter;

    public static void navTo(Context context) {
        Intent intent = new Intent(context, CoinDetailActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_detail);
        ToolBarOptions options = new NimToolBarOptions();
        options.titleId = R.string.settings;
        setToolBar(R.id.toolbar, options);
        setTitle("金币详情");
        mCoinDataSource = new CoinDataSource();
        initView();
        initData(false);
    }

    private void initData(boolean isLoadMore) {
        int page = 1;
        if (isLoadMore) {
            page = mPage + 1;
        }
        mCoinDataSource.getCoinDetail(DemoCache.getServerAccount(), page, new RequestMultiplyCallback<CoinDetailResponse>() {
            @Override
            public void onFail(BaseException e) {
                if (srlRefresh != null) srlRefresh.setRefreshing(false);
                if (isLoadMore) {
                    mAdapter.loadMoreFail();
                }
            }

            @Override
            public void onSuccess(CoinDetailResponse coinDetailResponse) {
                if (srlRefresh != null) srlRefresh.setRefreshing(false);
                if (coinDetailResponse == null) {
                    return;
                }
                if (coinDetailResponse.getRecords() == null || coinDetailResponse.getRecords().isEmpty()) {
                    if (isLoadMore) {
                        mAdapter.loadMoreEnd();
                    }
                } else {
                    if (isLoadMore) {
                        mPage++;
                        mAdapter.addData(coinDetailResponse.getRecords());
                        mAdapter.loadMoreComplete();
                    } else {
                        mAdapter.setNewData(coinDetailResponse.getRecords());
                        if (coinDetailResponse.getRecords().size() < 20) {
                            mAdapter.loadMoreEnd();
                        } else {
                            mAdapter.loadMoreComplete();
                        }
                    }
                }

            }
        });
    }

    private void initView() {
        rvContent = findView(R.id.rv_content);
        srlRefresh = findView(R.id.srl_refresh);
        srlRefresh.setOnRefreshListener(this);
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CoinDetailAdapter(rvContent);
        rvContent.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                rvContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData(true);
                    }
                }, 300);
            }
        });
    }

    @Override
    public void onRefresh() {
        initData(false);
    }
}
