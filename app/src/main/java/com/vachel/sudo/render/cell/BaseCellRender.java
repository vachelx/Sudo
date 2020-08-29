package com.vachel.sudo.render.cell;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.vachel.sudo.bean.CellTouchBean;

import java.util.TreeSet;

public abstract class BaseCellRender {
    final int mX;
    final int mY;
    final ICellCallback mCallback;
    final CommonParams mParams;

    public float mCenterX;
    public float mCenterY;

    public boolean mIsImmutable; // 为true时表示该cell为题目预先填的cell
    public Integer mCellValue = 0;


    public TreeSet<Integer> mMarkValues;

    public float mTextOffsetY;
    public int mStartAnimOffset;


    public BaseCellRender(int i, int j, CommonParams params, ICellCallback cb) {
        mX = i;
        mY = j;
        mParams = params;
        mCallback = cb;
    }

    public void initDataByExam(Integer value) {
        mCellValue = value;
        mIsImmutable = value != 0;
    }

    public void initLocation(float cellWidth) {
        mCenterX = cellWidth * mY + mParams.cellWidth / 2f;
        mCenterY = mParams.cellWidth * mX + mParams.cellWidth / 2f;
    }

    public abstract void onDraw(Canvas canvas, CellTouchBean touchCell);

    void resetTextSize() {
        mParams.textPaint.setTextSize(70f * getRelativeProgress(mCallback.getInflateAnimProgress()));
        Paint.FontMetrics fontMetrics = mParams.textPaint.getFontMetrics();
        mTextOffsetY = (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
    }


    public interface ICellCallback {
        int getInflateAnimProgress();
    }

    private float getRelativeProgress(int progress) {
        if (progress > mStartAnimOffset) {
            progress = progress - mStartAnimOffset;
            if (progress > 100) {
                progress = 100;
            }
        } else {
            progress = 0;
        }
        return progress / 100f;
    }
}
