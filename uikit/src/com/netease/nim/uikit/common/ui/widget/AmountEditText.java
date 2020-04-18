package com.netease.nim.uikit.common.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;

/**
 * Created by xxx on 2018/5/17.
 * 金额输入框
 */

public class AmountEditText extends android.support.v7.widget.AppCompatEditText implements TextWatcher {
    AmountInputFilter[] filters = {new AmountInputFilter()};
    private String s2 = "";
    private AmountTextStatusChangeListener changeListener;

    public AmountEditText(Context context) {
        this(context, null);
    }

    public AmountEditText(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public AmountEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI();
    }

    private void initUI() {
        addTextChangedListener(this);
        setFilters(filters);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String amountText = s.toString().trim();
        if (TextUtils.isEmpty(amountText)) {
            if (changeListener != null) {
                changeListener.onTextChange("0.00", false, false, s);
            }
        } else if (".".equals(amountText)) {
            if (changeListener != null) {
                changeListener.onTextChange("0.00", false, false, "0");
            }
        } else {
            String suStr = amountText.substring(amountText.indexOf(".") + 1);
            float amountNumber = Float.parseFloat(amountText);
            String showNumber = String.format("%.2f", amountNumber);
            if (amountText.contains(".")) {
                if (suStr.length() > 2) {
                    setText(showNumber);
                    setSelection(showNumber.length());
                }
            }
            if (amountNumber > 0 && amountNumber <= 200) {
                if (changeListener != null) {
                    changeListener.onTextChange(showNumber, true, false, s);
                }
            } else if (amountNumber > 200) {
                if (changeListener != null) {
                    changeListener.onTextChange(showNumber, false, true, s);
                }
            } else {
                if (changeListener != null) {
                    changeListener.onTextChange(s2, false, false, s);
                }
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    public void setAmountTextStatusChangeListener(AmountTextStatusChangeListener listener) {
        this.changeListener = listener;
    }

    public interface AmountTextStatusChangeListener {
        /**
         * @param text           显示的金额
         * @param isClick        是否能够点击支付
         * @param isShowDescribe 是否显示金额描述
         * @param etText         文本输入框所输入的数字
         */
        void onTextChange(String text, boolean isClick, boolean isShowDescribe, CharSequence etText);
    }
}
