package com.netease.nim.demo.dialog;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by it on 2017/3/8.
 * dialogfragment基类
 */

public abstract class BaseDialogFragment extends DialogFragment {
    public Activity mActivity;
    private View mView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        mView = inflater.inflate(getLayoutId(), container, true);
        init(mView);
        return mView;
    }
    protected <T extends View> T findView(int resId) {
        return (T) (mView.findViewById(resId));
    }
    protected abstract int getLayoutId();

    protected void init(View view) {

    }
    protected void setOnDialogListener(OnDialogListener onDialogListener) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
