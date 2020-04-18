package com.netease.nim.demo.main.fragment;

import android.os.Bundle;

import com.netease.nim.demo.R;
import com.netease.nim.demo.main.model.MainTab;
import com.netease.nim.uikit.common.activity.UI;

/**
 * 我的
 * Created by huangjun on 2015/12/11.
 */
public class MineListFragment extends MainTabFragment {

    private MineFragment fragment;

    public MineListFragment() {
        setContainerId(MainTab.MINE.fragmentId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onCurrent(); // 触发onInit，提前加载
    }

    @Override
    protected void onInit() {
        addContactFragment();  // 集成通讯录页面
    }

    // 将通讯录列表fragment动态集成进来。 开发者也可以使用在xml中配置的方式静态集成。
    private void addContactFragment() {
        fragment = new MineFragment();
        fragment.setContainerId(R.id.mine_fragment);

        UI activity = (UI) getActivity();

        // 如果是activity从堆栈恢复，FM中已经存在恢复而来的fragment，此时会使用恢复来的，而new出来这个会被丢弃掉
        fragment = (MineFragment) activity.addFragment(fragment);

    }

    @Override
    public void onCurrentTabClicked() {
        // 点击切换到当前TAB
        if (fragment != null) {
            fragment.initData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
