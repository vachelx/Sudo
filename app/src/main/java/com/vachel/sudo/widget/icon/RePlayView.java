package com.vachel.sudo.widget.icon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class RePlayView extends BaseIconView {

    private RectF mOval;

    public RePlayView(Context context) {
        super(context);
    }

    public RePlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RePlayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        canvas.drawArc(getOval(width, height), 0, 360, false, mPaint);

        int useSize = height / 8;
        mPath.reset();
        mPath.moveTo(width / 2f - useSize + mPaintWidth / 2f, height / 3f);
        mPath.lineTo(width / 2f - useSize + mPaintWidth / 2f, height * 2f / 3);
        mPath.lineTo(width / 2f + useSize + mPaintWidth / 2f, height / 2f);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }

    private RectF getOval(int width, int height) {
        if (mOval == null) {
            int offset = height / 6;
            int useSize = height / 2 - offset;
            mOval = new RectF(width / 2f - useSize, offset, width / 2f + useSize, height - offset);
        }
        return mOval;
    }
}
