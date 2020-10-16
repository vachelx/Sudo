package com.vachel.sudo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.vachel.sudo.R;
import com.vachel.sudo.widget.icon.BaseIconView;

public class IconView extends BaseIconView {

    private float mTextOffsetY;
    private float mDiffPadding = 16;
    private float mTextSize;
    private String[][] mText = new String[][]{
            {null, "3", null},
            {"1", "?", "7"},
            {null, "5", null}
    };
    private Bitmap mBitmap;
    private RectF mRectF;
    private int mWhiteA;

    public IconView(Context context) {
        super(context);
    }

    public IconView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public IconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initExtra(AttributeSet attrs) {
        mWhiteA = getContext().getResources().getColor(R.color.white_a);
        initBitmap();
    }

    private void initBitmap() {
        Drawable drawable = getResources().getDrawable(R.drawable.blue_app_icon_bg);
        mBitmap = Bitmap.createBitmap( drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(mBitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        getTextOffSetY();
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        float cellWidth = (width - 2 * mDiffPadding) / 3f;
        mPaint.setColor(mColorBlue);
        mPaint.setStyle(Paint.Style.FILL);
        if (mBitmap == null || mBitmap.isRecycled()) {
            initBitmap();
        }
        canvas.drawBitmap(mBitmap, null, mRectF, mPaint);
        mPaint.setColor(mWhiteA);
        canvas.drawLine(mDiffPadding + cellWidth, mDiffPadding, mDiffPadding + cellWidth, height- mDiffPadding, mPaint);
        canvas.drawLine(mDiffPadding + cellWidth*2, mDiffPadding, mDiffPadding + cellWidth*2, height- mDiffPadding, mPaint);
        canvas.drawLine(mDiffPadding, mDiffPadding + cellWidth, width-mDiffPadding, mDiffPadding + cellWidth, mPaint);
        canvas.drawLine(mDiffPadding, mDiffPadding + cellWidth*2, width-mDiffPadding, mDiffPadding + cellWidth*2, mPaint);
        mPaint.setColor(Color.WHITE);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String text = mText[i][j];
                if (!TextUtils.isEmpty(text)) {
                    float textWidth = mPaint.measureText(text);
                    canvas.drawText(text, mDiffPadding + i * cellWidth + cellWidth / 2f - textWidth / 2f, mDiffPadding + j * cellWidth + cellWidth / 2f + mTextOffsetY, mPaint);
                }
            }
        }
    }

    private float getTextOffSetY() {
        if (mTextOffsetY == 0) {
            float cellWidth = (getMeasuredWidth() - 2 * mDiffPadding) / 3f;
            mTextSize = (cellWidth - mDiffPadding * 2) * 3f/4;
            mPaint.setTextSize( mTextSize);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            mTextOffsetY = (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
            mRectF = new RectF();
            mRectF.left = mDiffPadding;
            mRectF.top = mDiffPadding;
            mRectF.right = getMeasuredWidth() - mDiffPadding;
            mRectF.bottom = getMeasuredHeight() - mDiffPadding;
        }
        return mTextOffsetY;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
    }
}
