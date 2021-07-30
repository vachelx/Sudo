package com.vachel.sudo.widget.icon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.vachel.sudo.R;

/**
 * Created by jianglixuan on 2020/8/25.
 * Describe:
 */
public class LevelItem extends BaseIconView {
    private String mLevelText;
    private Paint mBgPaint;
    private Paint mTextPaint;
    private int mColorGrey;
    private int mColorGreyDark;
    private final int mDiffPadding = 16;
    private float mTextSize;
    private float mTextOffsetY;
    private RectF mOval;
    private int mColorGreen;
    private String mTakeText;
    private boolean mHasArchive;
    private ILevelClickListener mListener;
    private Runnable mRunnable;

    public LevelItem(Context context) {
        super(context);
    }

    public LevelItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LevelItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initExtra(AttributeSet attrs) {
        mColorGrey = getContext().getResources().getColor(R.color.grey);
        mColorGreyDark = getContext().getResources().getColor(R.color.grey_dark);
        mColorGreen = getContext().getResources().getColor(R.color.default_color_green);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(100f);

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(mColorGrey);
        mBgPaint.setStrokeWidth(1f);
        setOnClickListener(v -> {
            if (mListener == null) {
                return;
            }
            if (mRunnable != null) {
                removeCallbacks(mRunnable);
                mListener.onClick(LevelItem.this, true);
                mRunnable = null;
            } else {
                mRunnable = () -> {
                    mListener.onClick(LevelItem.this, false);
                    mRunnable = null;
                };
                postDelayed(mRunnable, 250);
            }
        });
    }
    public void resetText(String levelText) {
        mLevelText = levelText;
        mTakeText = null;
        invalidate();
    }

    public void resetText(String levelText, String takeText) {
        mLevelText = levelText;
        mTakeText = takeText;
        invalidate();
    }

    public void setHasArchive(boolean hasArchive) {
        mHasArchive = hasArchive;
        invalidate();
    }

    public boolean hasNullTakeTime() {
        return TextUtils.isEmpty(mTakeText);
    }

    public void updateTakeTime(String takeTime) {
        mTakeText = takeTime;
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
        super.onDraw(canvas);
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();

        int center = width / 2;
        if (!isClickable()) {
            mBgPaint.setStyle(Paint.Style.STROKE);
            mBgPaint.setColor(mColorGrey);
            canvas.drawArc(getOval(width, height), 0, 360, false, mBgPaint);
        } else if (mIsPressed) {
            mBgPaint.setColor(mColorGreyDark);
            mBgPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(center, center, (width - mDiffPadding * 2) / 2f, mBgPaint);
        } else {
            mBgPaint.setStyle(Paint.Style.FILL);
            mBgPaint.setColor(isSelected() ? mColorBlue : mColorGreen);
            canvas.drawCircle(center, center, (width - mDiffPadding * 2) / 2f, mBgPaint);
        }

        if (!TextUtils.isEmpty(mLevelText)) {
            float offSetY = getTextOffSetY();
            float keyOffsetX = mTextPaint.measureText(mLevelText) / 2;
            mTextPaint.setTypeface(Typeface.DEFAULT);
            mTextPaint.setColor(isClickable() ? mHasArchive ? Color.YELLOW : Color.WHITE : mColorBlue);
            if (!TextUtils.isEmpty(mTakeText)) {
                canvas.drawText(mLevelText, center - keyOffsetX, height - mDiffPadding * 1.5f, mTextPaint);
                mTextPaint.setTypeface(Typeface.SERIF);
                mTextPaint.setColor(isSelected() && mHasArchive ? Color.YELLOW : Color.WHITE);
                canvas.drawText(mTakeText, center - mTextPaint.measureText(mTakeText) / 2f, center + offSetY - mDiffPadding / 2f, mTextPaint);
            } else {
                canvas.drawText(mLevelText, center - keyOffsetX, center + offSetY, mTextPaint);
            }
        }
    }

    private RectF getOval(int width, int height) {
        if (mOval == null) {
            mOval = new RectF(mDiffPadding, mDiffPadding, width - mDiffPadding, height - mDiffPadding);
        }
        return mOval;
    }

    private float getTextOffSetY() {
        if (mTextOffsetY == 0) {
            mTextSize = (getMeasuredWidth() - mDiffPadding * 2) * 1f / 3;
            mTextPaint.setTextSize(mTextSize);
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            mTextOffsetY = (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
        }
        return mTextOffsetY;
    }

    public interface ILevelClickListener{
        void onClick(View v, boolean isDouble);
    }

    public void setLevelClickListener(ILevelClickListener listener){
        mListener = listener;
    }

}
