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
        float useSize = height / 2f - offset;
        canvas.drawLine(width / 2f + useSize, height / 2f + height / 6f, width / 2f - useSize + mPaintWidth / 2f, height / 2f + height / 6f, mPaint);
        float locX = width / 2f - useSize + mPaintWidth;
        canvas.drawLine(locX, height / 2f + height / 6f, locX, height / 2f - height / 6f, mPaint);
        mPath.reset();
        mPath.moveTo(locX - mPaintWidth / 2f, height / 2f - height / 6f);
        mPath.lineTo(locX, height / 2f - height / 6f - mPaintWidth);
        mPath.lineTo(locX + mPaintWidth / 2f, height / 2f - height / 6f);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }
}
