package com.vachel.sudo.render.cell;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.vachel.sudo.bean.CellTouchBean;
import com.vachel.sudo.manager.ThemeManager;

import java.util.TreeSet;

public class CellRender extends BaseCellRender{
    public CellRender(int i, int j, CommonParams params, ICellCallback cb) {
        super(i, j, params, cb);
    }

    @Override
    public void onDraw(Canvas canvas, CellTouchBean touchCell) {
        Integer selectValue = touchCell.selectValue;
        int touchX = touchCell.x;
        int touchY = touchCell.y;
        float cellWidth = mParams.cellWidth;

        if (mCellValue != 0) {
            resetTextSize();
            String text = mCellValue.toString();
            float offsetX = mParams.textPaint.measureText(text) / 2;
            if (mCellValue.equals(selectValue)) {
                if (touchX == mX && touchY == mY) {
                    canvas.drawRect(cellWidth * mY, cellWidth * mX, cellWidth * (mY + 1), cellWidth * (mX + 1), mParams.rectPaint);
                }
                canvas.drawCircle(mCenterX, mCenterY, (cellWidth - 20) / 2, mParams.circlePaint);
            }
            mParams.textPaint.setColor(!mIsImmutable ? mCellValue.equals(selectValue) ? Color.WHITE : ThemeManager.getInstance().getDefaultThemeColor() : Color.BLACK);
            canvas.drawText(text, mCenterX - offsetX, mCenterY + mTextOffsetY, mParams.textPaint);
        } else {
            if (mCellValue.equals(selectValue) && touchX == mX && touchY == mY) {
                canvas.drawRect(cellWidth * mY, cellWidth * mX, cellWidth * (mY + 1), cellWidth * (mX + 1), mParams.rectPaint);
            }
            if (mMarkValues != null && mMarkValues.size() > 0) {
                float baseX = cellWidth * mY;
                float baseY = cellWidth * mX;
                for (Integer mark : mMarkValues) {
                    float markTextCenterX = baseX + ((mark - 1) % 3 + 0.5f) * cellWidth / 3;
                    float markTextCenterY = baseY + ((mark - 1) / 3 + 0.5f) * cellWidth / 3;
                    float offsetX = mParams.markPaint.measureText(mark.toString()) / 2;
                    canvas.drawText(mark.toString(), markTextCenterX - offsetX, markTextCenterY + mParams.markOffsetY, mParams.markPaint);
                }
            }
        }
    }

    public void drawCompleteProgress(Canvas canvas, int breathProgress) {
        mParams.textPaint.setColor(Color.WHITE);
        float radius = (mParams.cellWidth - 20) / 2f;
        if (breathProgress >= 0) {
            radius = radius * breathProgress / 100f;
        }

        int alpha = breathProgress >= 0 ? (int) (breathProgress * 1.0f / 100 * 255) : 255;
        boolean needUpdate = mParams.textPaint.getAlpha() != alpha;
        if (needUpdate) {
            mParams.textPaint.setAlpha(alpha);
            mParams.circlePaint.setAlpha(alpha);
            mParams.textPaint.setTextSize(70f * breathProgress / 100);
            Paint.FontMetrics fontMetrics = mParams.textPaint.getFontMetrics();
            mTextOffsetY = (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
        }

        float offsetX = mParams.textPaint.measureText(mCellValue.toString()) / 2;
        canvas.drawCircle(mCenterX, mCenterY, radius, mParams.circlePaint);
        canvas.drawText(mCellValue.toString(), mCenterX - offsetX, mCenterY + mTextOffsetY, mParams.textPaint);
    }

    public void performMarkValue(Integer value) {
        mCellValue = 0;
        if (mMarkValues == null) {
            mMarkValues = new TreeSet<>();
        }
        if (mMarkValues.contains(value)) {
            mMarkValues.remove(value);
        } else {
            mMarkValues.add(value);
        }
    }
}
