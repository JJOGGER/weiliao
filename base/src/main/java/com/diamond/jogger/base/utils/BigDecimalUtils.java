package com.diamond.jogger.base.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by xxx on 2018/5/21.
 * 字符串的加减乘除运算
 */

public class BigDecimalUtils {
    /**
     * 加法运算
     *
     * @param value1 被加数
     * @param value2 加数
     * @return
     */
    public static String add(String value1, String value2) {
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        double value = b1.add(b2).doubleValue();
        return String.format("%.2f", value);
    }

    /**
     * 加法运算 获取double值
     *
     * @param value1
     * @param value2
     * @return
     */
    public static double addDouble(String value1, String value2) {
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        return b1.add(b2).doubleValue();
    }


    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static String sub(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        double value = b1.subtract(b2).doubleValue();
        return String.format("%.2f", value);
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static String mul(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return String.valueOf(b1.multiply(b2).intValue());
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积(保留两位小数)
     */
    public static String mul2(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        double value = b1.multiply(b2).doubleValue();
        return String.format("%.2f", value);
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static long mulLong(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        long value = b1.multiply(b2).longValue();
        return value;
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mulDouble(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.multiply(b2).doubleValue();
    }

    /**
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static String div(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        double value = b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return String.format("%.2f", value);
    }

    /**
     * @param value1 被除数
     * @param value1 除数
     * @return 两个参数的商(long值)
     */
    public static long divLongDown(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);

        return b1.divide(b2, 0, BigDecimal.ROUND_DOWN).longValue();
    }

    /**
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double divDouble(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商(不保留小数点)
     */
    public static String divDouble0(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        double value = b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return String.format("%.0f", value);
    }

//    /**
//     * 格式化金额
//     *
//     * @param amount 金额
//     */
//    public static String formatAmountDouble(double amount) {
//        DecimalFormat formater = new DecimalFormat();
//        formater.setMaximumFractionDigits(2);
//        formater.setGroupingSize(3);
//        formater.setRoundingMode(RoundingMode.FLOOR);
//        String amountStr = formater.format(amount);
//        if (TextUtils.isEmpty(amountStr)) {
//            return "";
//        }
//        if (amountStr.contains(".")) {
//            if (amountStr.substring(amountStr.indexOf(".")).length() == 1) {
//                return amountStr + ".00";
//            } else if (amountStr.substring(amountStr.indexOf(".")).length() == 2) {
//                return amountStr + "0";
//            } else {
//                return amountStr;
//            }
//        } else {
//            return amountStr + ".00";
//        }
//    }

    /**
     * 格式化金额
     *
     * @param amount 金额
     */
    public static String formatAmount(float amount) {
        DecimalFormat formater = new DecimalFormat();
        formater.setMaximumFractionDigits(2);
        formater.setGroupingSize(3);
        formater.setRoundingMode(RoundingMode.FLOOR);
        String amountStr = formater.format(amount);
        if (TextUtils.isEmpty(amountStr)) {
            return "";
        }
        if (amountStr.contains(".")) {
            if (amountStr.substring(amountStr.indexOf(".")).length() == 1) {
                return amountStr + ".00";
            } else if (amountStr.substring(amountStr.indexOf(".")).length() == 2) {
                return amountStr + "0";
            } else {
                return amountStr;
            }
        } else {
            return amountStr + ".00";
        }
    }

    /**
     * 格式化金额
     *
     * @param amount String类型的金额
     */
    public static String formatAmount(String amount) {
        if (TextUtils.isEmpty(amount)) {
            return "0.00";
        } else {
            return formatAmount(Float.parseFloat(amount));
        }
    }
}
