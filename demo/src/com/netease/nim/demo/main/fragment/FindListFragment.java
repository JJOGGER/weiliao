package com.netease.nim.demo.main.fragment;

import android.os.Bundle;

import com.netease.nim.demo.R;
import com.netease.nim.demo.main.model.MainTab;
import com.netease.nim.uikit.common.activity.UI;


/**
 * <p/>
 * Created by huangjun on 2015/9/7.
 */
public class FindListFragment extends MainTabFragment {

    private FindFragment fragment;

    public FindListFragment() {
        setContainerId(MainTab.FIND.fragmentId);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        onCurrent(); // 触发onInit，提前加载
    }

    @Override
    protected void onInit() {
        addFindFragment();  //
    }

    // 将通讯录列表fragment动态集成进来。 开发者也可以使用在xml中配置的方式静态集成。
    private void addFindFragment() {
        fragment = new FindFragment();
        fragment.setContainerId(R.id.find_fragment);

        UI activity = (UI) getActivity();

        // 如果是activity从堆栈恢复，FM中已经存在恢复而来的fragment，此时会使用恢复来的，而new出来这个会被丢弃掉
        fragment = (FindFragment) activity.addFragment(fragment);

    }

    @Override
    public void onCurrentTabClicked() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
