package com.vachel.sudo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vachel.sudo.R;
import com.vachel.sudo.widget.icon.BaseIconView;


/**
 * Created by jianglixuan on 2020/8/27.
 * Describe:
 */
public class TrapezoidView extends BaseIconView {

    private boolean mOrientationRight;
    private String mText;
    private Paint mTextPaint;
    private float mTextOffsetY;
    private float mTextOffsetX;
    private int mDarkBlue;

    public TrapezoidView(Context context) {
        super(context);
    }

    public TrapezoidView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TrapezoidView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initExtra(AttributeSet attrs) {
        mPaint.setStyle(Paint.Style.FILL);
        mDarkBlue = getContext().getResources().getColor(R.color.default_text_color);

        if (mText == null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.vachel);
            mOrientationRight = a.getBoolean(R.styleable.vachel_trap_orientation_right, true);
            mText = a.getString(R.styleable.vachel_trap_text);
            a.recycle();
        }

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setStrokeWidth(mPaintWidth);
        if (!TextUtils.isEmpty(mText)){
            int textSize = getContext().getResources().getDimensionPixelSize(R.dimen.default_text_size);
            mTextPaint.setTextSize(textSize);
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            mTextOffsetY = (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
            mTextOffsetX = mTextPaint.measureText(mText);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(isPressed() || isSelected() || mIsPressed ? mDarkBlue: mColorBlue);
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        mPath.reset();
        float offset = height < width ? height : width / 4f;
        if (mOrientationRight) {
            mPath.moveTo(0, 0);
            mPath.lineTo(width - offset, 0);
            mPath.lineTo(width, height);
            mPath.lineTo(0, height);
        } else {
            mPath.moveTo(width, height);
            mPath.lineTo(width, 0);
            mPath.lineTo(offset, 0);
            mPath.lineTo(0, height);
        }
        mPath.close();
        canvas.drawPath(mPath, mPaint);

        if (!TextUtils.isEmpty(mText)) {
            if (mOrientationRight) {
                canvas.drawText(mText, 30, height / 2f + mTextOffsetY, mTextPaint);
            } else {
                canvas.drawText(mText, width - 30 - mTextOffsetX, height / 2f + mTextOffsetY, mTextPaint);
            }
        }
    }

    public void setText(@NonNull CharSequence text){
        mText = text.toString();
        mTextOffsetX = mTextPaint.measureText(mText);
        invalidate();
    }
}
