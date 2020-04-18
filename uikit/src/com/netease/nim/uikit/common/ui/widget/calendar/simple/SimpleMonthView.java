package com.netease.nim.uikit.common.ui.widget.calendar.simple;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ui.widget.calendar.Calendar;
import com.netease.nim.uikit.common.ui.widget.calendar.CalendarUtil;
import com.netease.nim.uikit.common.ui.widget.calendar.MonthView;


/**
 * 高仿魅族日历布局
 * Created by huanghaibin on 2017/11/15.
 */

public class SimpleMonthView extends MonthView {

    private int mRadius;
    private float mRadio;
    private int mPadding;
    private Paint mTextPaint = new Paint();
    private Paint mSchemeBasicPaint = new Paint();
    private float mSchemeBaseLine;

    public SimpleMonthView(Context context) {
        super(context);
        mRadio = CalendarUtil.dipToPx(getContext(), 7);
        mPadding = CalendarUtil.dipToPx(getContext(), 4);
        mTextPaint.setTextSize(CalendarUtil.dipToPx(context, 8));
        mTextPaint.setColor(0xff888888);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFakeBoldText(true);

        mSchemeBasicPaint.setAntiAlias(true);
        mSchemeBasicPaint.setStyle(Paint.Style.FILL);
        mSchemeBasicPaint.setTextAlign(Paint.Align.CENTER);
        mSchemeBasicPaint.setColor(0xffed5353);
        mSchemeBasicPaint.setFakeBoldText(true);
        Paint.FontMetrics metrics = mSchemeBasicPaint.getFontMetrics();
        mSchemeBaseLine = mRadio - metrics.descent + (metrics.bottom - metrics.top) / 2 + CalendarUtil.dipToPx(getContext(), 1);

        //兼容硬件加速无效的代码
        setLayerType(View.LAYER_TYPE_SOFTWARE, mSelectedPaint);
        //4.0以上硬件加速会导致无效
        mSelectedPaint.setMaskFilter(new BlurMaskFilter(25, BlurMaskFilter.Blur.SOLID));
    }

    @Override
    protected void onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 9 * 4;
        mSchemePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onLoopStart(int x, int y) {

    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2 + 16;
//        mSelectedPaint.setColor(getResources().getColor(R.color.colorPrimary));
        canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
        String text= TextUtils.isEmpty(calendar.getScheme())?"今日":calendar.getScheme();
        mTextPaint.setColor(Color.WHITE);
        canvas.drawText(text,
                cx - getTextWidth(text) / 2,
                y + mItemHeight * 4 / 5, mTextPaint);
        return true;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
//        canvas.drawCircle(cx, cy, mRadius, mSchemePaint);
//        canvas.drawText(calendar.getScheme(),
//                x + mItemWidth - mPadding - mRadio / 2 - getTextWidth(calendar.getScheme()) / 2,
//                y + mPadding + mSchemeBaseLine, mTextPaint);
        canvas.drawCircle(cx, y + mItemHeight * 3 / 5, mRadius, mSchemePaint);
        mTextPaint.setColor(getResources().getColor(R.color.color_grey_999999));
        canvas.drawText(calendar.getScheme(),
                cx - getTextWidth(calendar.getScheme()) / 2,
                y + mItemHeight * 4 / 5, mTextPaint);
    }

    /**
     * 获取字体的宽
     *
     * @param text text
     * @return return
     */
    private float getTextWidth(String text) {
        return mTextPaint.measureText(text);
    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        float baselineY = mTextBaseLine + y;
        int cx = x + mItemWidth / 2;

        if (isSelected) {
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    mSelectTextPaint);
        } else if (hasScheme) {
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mSchemeTextPaint : mOtherMonthTextPaint);

        } else {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);
        }
    }
}
