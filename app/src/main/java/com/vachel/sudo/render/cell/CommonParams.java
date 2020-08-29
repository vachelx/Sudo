package com.vachel.sudo.render.cell;

import android.graphics.Color;
import android.graphics.Paint;

import com.vachel.sudo.manager.ThemeManager;

// cell公用不会变的参数
public class CommonParams {
    public Paint textPaint;
    public Paint circlePaint;
    public Paint rectPaint;
    public Paint markPaint;
    public float cellWidth;
    public float markOffsetY;

    public CommonParams() {
        int mColorBlue = ThemeManager.getInstance().getDefaultThemeColor();
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(mColorBlue);
        circlePaint.setStrokeWidth(6);

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setColor(Color.LTGRAY);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(mColorBlue);

        markPaint = new Paint();
        markPaint.setAntiAlias(true);
        markPaint.setColor(Color.BLACK);
        markPaint.setTextSize(22f);
        Paint.FontMetrics metrics = markPaint.getFontMetrics();
        markOffsetY = (metrics.descent - metrics.ascent) / 2f - metrics.descent;
    }
}
