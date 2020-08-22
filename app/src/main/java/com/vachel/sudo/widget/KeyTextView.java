package com.vachel.sudo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.vachel.sudo.R;

public class KeyTextView extends View {
    Paint mTextPaint;
    private Paint mBgPaint;
    float mTextOffsetY;
    private String mKetText;
    private String mCountText;
    float mTextSize;
    float mLiteOffsetY;
    private boolean mIsPressed;
    private boolean mIsMark;
    private int mColorBlue;
    private int mColorGrey;
    private int mColorGreyDark;

    public KeyTextView(Context context) {
        this(context, null);
    }

    public KeyTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        init();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.vachel);
        mKetText = a.getString(R.styleable.vachel_key_text);
        mIsMark = a.getBoolean(R.styleable.vachel_edit_mark, false);
        a.recycle();
    }

    private void init() {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(100f);

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);

        mColorBlue = getContext().getResources().getColor(R.color.main_blue);
        mColorGrey = getContext().getResources().getColor(R.color.grey);
        mColorGreyDark = getContext().getResources().getColor(R.color.grey_dark);
    }

    public void setCountText(String count) {
        mCountText = count;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int measuredWidth = getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int center = getMeasuredWidth() / 2;
        mBgPaint.setColor(mIsPressed || isSelected() ? mColorGreyDark : mColorGrey);
        canvas.drawCircle(center, center, (getMeasuredWidth() - 32) / 2, mBgPaint);

        float offSetY = getTextOffSetY();

        mTextPaint.setColor(mIsPressed || isSelected() ? Color.WHITE : mColorBlue);
        mTextPaint.setTextSize(mTextSize);
        float keyOffsetX = mTextPaint.measureText(mKetText) / 2;
        canvas.drawText(mKetText, center - keyOffsetX, center + offSetY, mTextPaint);

        if (!TextUtils.isEmpty(mCountText)) {
            mTextPaint.setTextSize(mTextSize / 4);
            canvas.drawText(mCountText, center - mTextPaint.measureText(mCountText) / 2, center + offSetY + mLiteOffsetY * 3, mTextPaint);
        }

        if (mIsMark) {
            mTextPaint.setTextSize(mTextSize / 5);
            String text = "1";
            canvas.drawText(text, center - mTextPaint.measureText(text) - keyOffsetX, center - offSetY, mTextPaint);
            text = "2";
            canvas.drawText(text, center + keyOffsetX, center - offSetY, mTextPaint);
            text = "3";
            canvas.drawText(text, center - keyOffsetX - mTextPaint.measureText(text), center + offSetY + mLiteOffsetY * 1.6f, mTextPaint);
            text = "4";
            canvas.drawText(text, center + keyOffsetX, center + offSetY + mLiteOffsetY * 1.6f, mTextPaint);
        }
    }

    private float getTextOffSetY() {
        if (mTextOffsetY == 0) {
            mTextSize = (getMeasuredWidth() - 32) * 2f / 3;
            mTextPaint.setTextSize(mTextSize);
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            mTextOffsetY = (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;

            mTextPaint.setTextSize(mTextSize / 4);
            Paint.FontMetrics fontMetricsLite = mTextPaint.getFontMetrics();
            mLiteOffsetY = (fontMetricsLite.descent - fontMetricsLite.ascent) / 2 - fontMetricsLite.descent;
        }
        return mTextOffsetY;
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
