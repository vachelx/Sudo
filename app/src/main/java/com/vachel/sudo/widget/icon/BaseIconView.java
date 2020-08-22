package com.vachel.sudo.widget.icon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.vachel.sudo.R;

public abstract class BaseIconView extends View {
    Paint mPaint;
    Path mPath;
    int mPaintWidth = 8;
    private boolean mIsPressed;
    private int mColorBlue;

    public BaseIconView(Context context) {
        this(context, null);
    }

    public BaseIconView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseIconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        mColorBlue = getContext().getResources().getColor(R.color.main_blue);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mColorBlue);
        mPaint.setStrokeWidth(mPaintWidth);

        mPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(mIsPressed || isSelected() ? Color.WHITE : mColorBlue);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean touchEvent = super.onTouchEvent(event);
        if (isClickable()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mIsPressed = true;
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mIsPressed = false;
                    invalidate();
                    break;
            }
        }
        return touchEvent;
    }

    @Override
    public void setClickable(boolean clickable) {
        super.setClickable(clickable);
        setAlpha(clickable ? 1.0f : 0.37f);
    }
}
