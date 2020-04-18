package com.netease.nim.demo.redpacket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.diamond.jogger.base.entity.IntoRecordResponse;
import com.diamond.jogger.base.http.basic.callback.RequestMultiplyCallback;
import com.diamond.jogger.base.http.basic.exception.base.BaseException;
import com.diamond.jogger.base.http.datasource.CoinDataSource;
import com.diamond.jogger.base.http.datasource.base.ICoinDataSource;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.main.model.Extras;
import com.netease.nim.demo.redpacket.adapter.CoinIntoRecordAdapter;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;

public class CoinIntoRecordActivity extends UI implements SwipeRefreshLayout.OnRefreshListener {
    public static final int TYPE_IN = 0;
    public static final int TYPE_OUT = 1;
    private RecyclerView rvContent;
    private SwipeRefreshLayout srlRefresh;
    private CoinIntoRecordAdapter mAdapter;
    private int mType;
    private ICoinDataSource mCoinDataSource;
    private int mPage = 1;

    public static void navTo(Context context, int type) {
        Intent intent = new Intent(context, CoinIntoRecordActivity.class);
        intent.putExtra(Extras.TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_into_record);
        ToolBarOptions options = new NimToolBarOptions();
        setToolBar(R.id.toolbar, options);
        mType = getIntent().getIntExtra(Extras.TYPE, TYPE_OUT);
        initView();
        mCoinDataSource = new CoinDataSource();
        initData(false);
    }

    private void initData(boolean isLoadMore) {
        int page = 1;
        if (isLoadMore) {
            page = mPage + 1;
        }
        if (mType == TYPE_IN) {
            mCoinDataSource.getTurnInList(DemoCache.getServerAccount(), page, new RequestMultiplyCallback<IntoRecordResponse>() {
                @Override
                public void onFail(BaseException e) {
                    if (srlRefresh != null) srlRefresh.setRefreshing(false);
                    if (isLoadMore) {
                        mAdapter.loadMoreFail();
                    }
                }

                @Override
                public void onSuccess(IntoRecordResponse intoRecordResponse) {
                    if (srlRefresh != null) srlRefresh.setRefreshing(false);
                    if (intoRecordResponse == null) {
                        return;
                    }
                    if (intoRecordResponse.getRecords() == null || intoRecordResponse.getRecords().isEmpty()) {
                        if (isLoadMore) {
                            mAdapter.loadMoreEnd();
                        }
                    } else {
                        if (isLoadMore) {
                            mPage++;
                            mAdapter.addData(intoRecordResponse.getRecords());
                            mAdapter.loadMoreComplete();
                        } else {
                            mAdapter.setNewData(intoRecordResponse.getRecords());
                            if (intoRecordResponse.getRecords().size() < 20) {
                                mAdapter.loadMoreEnd();
                            } else {
                                mAdapter.loadMoreComplete();
                            }
                        }
                    }
                }
            });
        } else {
            mCoinDataSource.getTurnOutList(DemoCache.getServerAccount(), page, new RequestMultiplyCallback<IntoRecordResponse>() {
                @Override
                public void onFail(BaseException e) {
                    if (srlRefresh != null) srlRefresh.setRefreshing(false);
                    if (isLoadMore) {
                        mAdapter.loadMoreFail();
                    }
                }

                @Override
                public void onSuccess(IntoRecordResponse intoRecordResponse) {
                    if (srlRefresh != null) srlRefresh.setRefreshing(false);
                    if (intoRecordResponse == null) {
                        return;
                    }
                    if (intoRecordResponse.getRecords() == null || intoRecordResponse.getRecords().isEmpty()) {
                        if (isLoadMore) {
                            mAdapter.loadMoreEnd();
                        }
                    } else {
                        if (isLoadMore) {
                            mPage++;
                            mAdapter.addData(intoRecordResponse.getRecords());
                            mAdapter.loadMoreComplete();
                        } else {
                            mAdapter.setNewData(intoRecordResponse.getRecords());
                            if (intoRecordResponse.getRecords().size() < 20) {
                                mAdapter.loadMoreEnd();
                            } else {
                                mAdapter.loadMoreComplete();
                            }
                        }
                    }
                }
            });
        }
    }

    private void initView() {
        rvContent = findView(R.id.rv_content);
        srlRefresh = findView(R.id.srl_refresh);
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CoinIntoRecordAdapter(rvContent);
        rvContent.setAdapter(mAdapter);
        srlRefresh.setOnRefreshListener(this);
        mAdapter.setOnLoadMoreListener(() -> rvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                initData(true);
            }
        }, 300));
        if (mType == TYPE_IN) {
            setTitle("转入记录");
        } else {
            setTitle("转出记录");
        }
    }

    @Override
    public void onRefresh() {
        initData(false);
    }
}
