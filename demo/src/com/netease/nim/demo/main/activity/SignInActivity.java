package com.netease.nim.demo.main.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.diamond.jogger.base.entity.SignData;
import com.diamond.jogger.base.entity.SignResponse;
import com.diamond.jogger.base.entity.SimpleUserInfo;
import com.diamond.jogger.base.http.basic.callback.RequestMultiplyCallback;
import com.diamond.jogger.base.http.basic.exception.base.BaseException;
import com.diamond.jogger.base.http.datasource.CoinDataSource;
import com.diamond.jogger.base.http.datasource.UserDataSource;
import com.diamond.jogger.base.http.datasource.base.ICoinDataSource;
import com.diamond.jogger.base.http.datasource.base.IUserDataSource;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.diamond.jogger.base.event.UserInfoEvent;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.widget.calendar.Calendar;
import com.netease.nim.uikit.common.ui.widget.calendar.CalendarView;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SignInActivity extends UI {
    private CalendarView cvDate;
    private ICoinDataSource mCoinDataSource;
    private SimpleDateFormat mSimpleDateFormat;
    private Button btnSignIn;
    private List<SignData> mSignDatas;
    private TextView tvTitle;
    private IUserDataSource mUserDataSource;


    public static void navTo(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ToolBarOptions options = new NimToolBarOptions();
        setToolBar(R.id.toolbar, options);
        setTitle("签到记录");
        mCoinDataSource = new CoinDataSource();
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        mUserDataSource = new UserDataSource();
        findViews();
        initData();
    }

    private void initData() {
        mCoinDataSource.getSignList(DemoCache.getServerAccount(), new RequestMultiplyCallback<List<SignData>>() {
            @Override
            public void onFail(BaseException e) {

            }

            @Override
            public void onSuccess(List<SignData> signDatas) {
                if (signDatas == null) return;
                mSignDatas = signDatas;
                initCalander();
            }

        });
    }

    private void initCalander() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        Map<String, Calendar> map = new HashMap<>();
        try {
            for (int i = 0; i < mSignDatas.size(); i++) {
                SignData signData = mSignDatas.get(i);
                Date date = mSimpleDateFormat.parse(signData.getDate());
                calendar.setTime(date);
                int year = calendar.get(java.util.Calendar.YEAR);
                int month = calendar.get(java.util.Calendar.MONTH) + 1;
                int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
                if (i == mSignDatas.size() - 1) {
                    tvTitle.setText(year + "年" + month + "月");
                    cvDate.scrollToCalendar(year, month
                            , day);
                    btnSignIn.setEnabled(!signData.isSignFlag());
                    btnSignIn.setEnabled(true);
                }
                map.put(getSchemeCalendar(year, month, i + 1, signData.isSignFlag()).toString(),
                        getSchemeCalendar(year, month, day, signData.isSignFlag()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cvDate.setSchemeDate(map);
    }

    private void findViews() {
        cvDate = findView(R.id.cv_date);
        btnSignIn = findView(R.id.btn_sign_in);
        tvTitle = findView(R.id.tv_title);
        btnSignIn.setOnClickListener(v -> {
            DialogMaker.showProgressDialog(SignInActivity.this, "签到中...");
            mCoinDataSource.sign(DemoCache.getServerAccount(), new RequestMultiplyCallback<SignResponse>() {
                @Override
                public void onFail(BaseException e) {
                    DialogMaker.dismissProgressDialog();
                    ToastHelper.showToast(getApplicationContext(), e.getMessage());
                }

                @Override
                public void onSuccess(SignResponse signResponse) {
                    DialogMaker.dismissProgressDialog();
                    ToastHelper.showToast(getApplicationContext(), "获得" + signResponse.getAmount() + "金币");
                    btnSignIn.setEnabled(false);
                    getUserInfo();
                    mSignDatas.get(mSignDatas.size() - 1).setSignFlag(true);
                    initCalander();
                }
            });
        });
//        cvDate.setThemeColor(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimary));
//        java.util.Calendar calendar = java.util.Calendar.getInstance();
//        calendar.setTime(new Date());
//        int year = calendar.get(java.util.Calendar.YEAR);
//        int month = calendar.get(java.util.Calendar.MONTH) + 1;
//        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
//        cvDate.scrollToCalendar(year, month
//                , day);
//        Map<String, Calendar> map = new HashMap<>();
//        for (int i = 1; i < day; i++) {
//            map.put(getSchemeCalendar(year, month, i, 0xFFFFFFFF, "未签").toString(),
//                    getSchemeCalendar(year, month, i, 0xFFFFFFFF, "未签"));
//        }
//        map.put(getSchemeCalendar(2019, 12, 3, 0xFFFFFFFF, "未签").toString(),
//                getSchemeCalendar(2019, 12, 3, 0xFFFFFFFF, "未签"));
//        map.put(getSchemeCalendar(2019, 12, 6, 0xFFFFFFFF, "未签").toString(),
//                getSchemeCalendar(2019, 12, 6, 0xFFFFFFFF, "未签"));
//        map.put(getSchemeCalendar(2019, 12, 9, 0xFFFFFFFF, "未签").toString(),
//                getSchemeCalendar(2019, 12, 9, 0xFFFFFFFF, "未签"));
//        map.put(getSchemeCalendar(2019, 12, 13, 0xFFFFFFFF, "未签").toString(),
//                getSchemeCalendar(2019, 12, 13, 0xFFFFFFFF, "未签"));
//        map.put(getSchemeCalendar(2019, 12, 14, 0xFFFFFFFF, "未签").toString(),
//                getSchemeCalendar(2019, 12, 14, 0xFFFFFFFF, "未签"));
//        map.put(getSchemeCalendar(2019, 12, 15, 0xFFFFFFFF, "未签").toString(),
//                getSchemeCalendar(2019, 12, 15, 0xFFFFFFFF, "未签"));
//        map.put(getSchemeCalendar(2019, 12, 18, 0xFFFFFFFF, "未签").toString(),
//                getSchemeCalendar(2019, 12, 18, 0xFFFFFFFF, "未签"));
//        cvDate.setSchemeDate(map);
    }

    private void getUserInfo() {
        mUserDataSource.getUserInfo(DemoCache.getServerAccount(), new RequestMultiplyCallback<SimpleUserInfo>() {
            @Override
            public void onFail(BaseException e) {

            }

            @Override
            public void onSuccess(SimpleUserInfo simpleUserInfo) {
                DemoCache.setSimpleUserInfo(simpleUserInfo);
                EventBus.getDefault().post(new UserInfoEvent(UserInfoEvent.ACTION_USER_INFO_UPDATE));
            }
        });
    }

    @SuppressWarnings("all")
    private Calendar getSchemeCalendar(int year, int month, int day, boolean flag) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        if (flag) {
//            Calendar.Scheme scheme=new Calendar.Scheme(Color.parseColor("40db25"),"已签");
            calendar.setScheme("已签");
            calendar.setSchemeColor(Color.GREEN);//如果单独标记颜色、则会使用这个颜色
        } else {
//            Calendar.Scheme scheme=new Calendar.Scheme(Color.parseColor("40db25"),"未签");
            calendar.setScheme("未签");
            calendar.setSchemeColor(Color.WHITE);//如果单独标记颜色、则会使用这个颜色
        }
        return calendar;
    }
}
