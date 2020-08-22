package com.vachel.sudo.widget.icon;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class UndoIcon extends BaseIconView {
    public UndoIcon(Context context) {
        super(context);
    }

    public UndoIcon(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public UndoIcon(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();

        float offset = height * 1.0f / 5;
        float useSize = height / 2 - offset;
        canvas.drawLine(width / 2 + useSize, height / 2 + height / 6, width / 2 - useSize + mPaintWidth / 2, height / 2 + height / 6, mPaint);
        float locX = width / 2 - useSize + mPaintWidth;
        canvas.drawLine(locX, height / 2 + height / 6, locX, height / 2 - height / 6, mPaint);
        mPath.reset();
        mPath.moveTo(locX - mPaintWidth / 2, height / 2 - height / 6);
        mPath.lineTo(locX, height / 2 - height / 6 - mPaintWidth);
        mPath.lineTo(locX + mPaintWidth / 2, height / 2 - height / 6);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }
}
