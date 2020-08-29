package com.vachel.sudo.render;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;

public class InnerLinesRender {
    private final Paint mInnerPaint;
    private float[] mLines;

    public InnerLinesRender() {
        mInnerPaint = new Paint();
        mInnerPaint.setAntiAlias(true);
        mInnerPaint.setColor(Color.LTGRAY);
    }

    public void drawInnerLines(Canvas canvas, float cellWidth) {
        canvas.drawLines(getLines(cellWidth), mInnerPaint);
    }

    // 单元格分割线
    private float[] getLines(float cellWidth) {
        if (mLines == null) {
            mLines = getInnerLines(cellWidth);
        }
        return mLines;
    }

    private float[] getInnerLines(float cellWidth) {
        ArrayList<Float> lines = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (i % 3 != 0) {
                    lines.add(cellWidth * j);
                    lines.add(cellWidth * i);
                    lines.add(cellWidth * (j + 1));
                    lines.add(cellWidth * i);
                }

                if (i % 3 != 2) {
                    lines.add(cellWidth * j);
                    lines.add(cellWidth * (i + 1));
                    lines.add(cellWidth * (j + 1));
                    lines.add(cellWidth * (i + 1));
                }

                if (j % 3 != 0) {
                    lines.add(cellWidth * j);
                    lines.add(cellWidth * (i));
                    lines.add(cellWidth * (j));
                    lines.add(cellWidth * (i + 1));
                }

                if (j % 3 != 2) {
                    lines.add(cellWidth * (j + 1));
                    lines.add(cellWidth * (i));
                    lines.add(cellWidth * (j + 1));
                    lines.add(cellWidth * (i + 1));
                }
            }
        }
        float[] innerLines = new float[lines.size()];
        for (int k = 0; k < lines.size(); k++) {
            innerLines[k] = lines.get(k);
        }
        return innerLines;
    }
}
