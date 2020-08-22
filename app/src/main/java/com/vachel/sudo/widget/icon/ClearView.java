package com.vachel.sudo.widget.icon;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class ClearView extends BaseIconView {
    public ClearView(Context context) {
        super(context);
    }

    public ClearView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ClearView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();

        int center = width / 2;
        float offset = height * 1f / 4;
        canvas.drawLine(center + offset, offset, center - offset, offset*3, mPaint);
        canvas.drawLine(center - offset, offset, center + offset, offset*3, mPaint);
    }
}
