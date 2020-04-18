package com.netease.nim.uikit.common.ui.widget;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xxx on 2018/5/17.
 * 过滤用户输入只能为金额格式
 */

public class AmountInputFilter implements InputFilter {
    Pattern mPattern;

    //输入的最大金额
    private static final int MAX_VALUE = Integer.MAX_VALUE;
    //小数点后的位数
    private static final int POINTER_LENGTH = 2;
    private static final String POINTER = ".";
    private static final String ZERO = "0";

    public AmountInputFilter() {
        mPattern = Pattern.compile("([0-9]|\\.)*");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String sourceText = source.toString();
        String destText = dest.toString();

        //验证删除等按键
        if (TextUtils.isEmpty(sourceText)) {
            return "";
        }
        if (!TextUtils.isEmpty(destText) && !destText.startsWith(".") && dstart == 0 && "0".equals(sourceText)) {
            return "";
        }
//        if (destText.length() >= 6) {
//            return "";
//        }
        Matcher matcher = mPattern.matcher(source);
        //已经输入小数点的情况下，只能输入数字
        if (destText.contains(POINTER)) {
            //判断输入的是否是mPattern里面的数据,既0~9级.,如果不是则不让其输入
            if (!matcher.matches()) {
                return "";
            } else {
                if (POINTER.equals(source)) {  //只能输入一个小数点
                    return "";
                }
            }
            //验证小数点精度，保证小数点后只能输入两位
            int index = destText.indexOf(POINTER);
            //截取已经输入数据点后面的String,
            String spotString = destText.substring(destText.indexOf(POINTER));
            //获取截取下来的长度,
            int length = spotString.length();
            //如果该长度大于2则表示小数点后面已经有了两位,则不让去输入
            if (length > POINTER_LENGTH && destText.indexOf(".") < dstart) {
                return "";
            } else {
                String str1 = destText.substring(0, destText.indexOf("."));
                if (!TextUtils.isEmpty(str1) && !".".equals(str1)) {
                    float a = Float.parseFloat(str1);
                    String str = destText.substring(destText.indexOf("."));
                    if (a <= 0 && dstart == 1 && str.length() >= 2) {
                        return "";
                    }
                }
            }
        } else {
            //没有输入小数点的情况下，只能输入小数点和数字，但首位不能输入小数点和0
            if (!matcher.matches()) {
                return "";
            } else {
                if (destText.startsWith("0") && !POINTER.equals(source)) {
                    if (dstart != 0) {
                        return "";
                    }
                } else {
                    if ((POINTER.equals(source)) && TextUtils.isEmpty(destText)) {
                        return "";
                    }
                }
            }
        }

        //验证输入金额的大小
        double sumText = Double.parseDouble(destText + sourceText);
        if (sumText > MAX_VALUE) {
            return dest.subSequence(dstart, dend);
        }
        String s = dest.subSequence(dstart, dend) + sourceText;
        return s;
    }
}
