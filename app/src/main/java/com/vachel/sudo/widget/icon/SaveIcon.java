package com.vachel.sudo.widget.icon;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class SaveIcon extends BaseIconView {
    public SaveIcon(Context context) {
        super(context);
    }

    public SaveIcon(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SaveIcon(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();

        float offset = height * 1.0f / 5;
        float useSize = height / 2 - offset;
        canvas.drawLine(width/2 - useSize, height * 1f / 2 - mPaintWidth / 6, width/2 - useSize/2 + mPaintWidth / 5, height * 2f / 3 + mPaintWidth / 6, mPaint);
        canvas.drawLine(width/2 - useSize/2  - mPaintWidth / 5, height * 2f / 3 + mPaintWidth / 6, width/2 + useSize, height * 1f / 3, mPaint);
    }
}
