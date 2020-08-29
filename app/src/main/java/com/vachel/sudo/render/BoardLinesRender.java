package com.vachel.sudo.render;

import android.graphics.Canvas;
import android.graphics.Paint;

public class BoardLinesRender {
    private float[] mBoardLines;

    public void drawLines(Canvas canvas, float cellWidth, Paint paint) {
        canvas.drawLines(getBoardLines(cellWidth * 3f), paint);
    }

    public float[] getBoardLines(float perWidth) {
        if (mBoardLines == null) {
            mBoardLines = new float[]{
                    0, perWidth, perWidth * 3, perWidth,
                    0, perWidth * 2, perWidth * 3, perWidth * 2,
                    perWidth, 0, perWidth, perWidth * 3,
                    perWidth * 2, 0, perWidth * 2, perWidth * 3
            };
        }
        return mBoardLines;
    }
}
