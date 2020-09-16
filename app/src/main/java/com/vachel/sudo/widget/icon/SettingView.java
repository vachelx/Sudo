package com.vachel.sudo.widget.icon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class SettingView extends BaseIconView {
    public SettingView(Context context) {
        super(context);
    }

    public SettingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int measuredWidth = getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
//        mPaint.setStrokeWidth(3f);
        float offset = height / 6f;
        float useSize = (height / 2f - offset) / 2f;
//        canvas.drawCircle(width / 2f, height / 2f, width * 5f / 6 / 2, mPaint);
        mPaint.setStrokeWidth(mPaintWidth);
        mPaint.setColor(Color.WHITE);
//        canvas.drawLine(width / 2f - useSize, height / 2f, width / 2f + useSize*1.5f, height / 2f, mPaint);
        canvas.drawLine(width / 2f - useSize*1.3f, height / 2f, width / 2f + useSize*1.5f, height / 2f, mPaint);
        canvas.drawLine(width / 2f - useSize*1.3f, height / 2f - useSize , width / 2f + useSize, height / 2f - useSize , mPaint);
        canvas.drawLine(width / 2f - useSize*1.3f, height / 2f + useSize , width / 2f + useSize, height / 2f + useSize, mPaint);

    }
}
