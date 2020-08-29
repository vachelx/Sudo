package com.vachel.sudo.render;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;

import static com.vachel.sudo.utils.Constants.ERROR_RECT_WIDTH;

public class EdgeGradientRender {

    private final Paint mGradientPaint;
    private LinearGradient[] mGradient= new LinearGradient[4];

    public EdgeGradientRender(){
        mGradientPaint = new Paint();
        mGradientPaint.setAntiAlias(true);
        mGradientPaint.setStyle(Paint.Style.FILL);
    }

    public void drawEdge(Canvas canvas, int width, int height){
        mGradientPaint.setShader(mGradient[0]);
        canvas.drawRect(0, 0, ERROR_RECT_WIDTH, height, mGradientPaint);
        mGradientPaint.setShader(mGradient[1]);
        canvas.drawRect(0, 0, width, ERROR_RECT_WIDTH, mGradientPaint);
        mGradientPaint.setShader(mGradient[2]);
        canvas.drawRect(width - ERROR_RECT_WIDTH, 0, width, height, mGradientPaint);
        mGradientPaint.setShader(mGradient[3]);
        canvas.drawRect(0, height - ERROR_RECT_WIDTH, width, height, mGradientPaint);
    }

    public void updateGradient(int width, int height, int progress){
        int alpha = (int) (progress / 100f * 255);
        int startColor = Color.argb(alpha, 255, 102, 102);
        mGradient[0] = new LinearGradient(0, 0,ERROR_RECT_WIDTH, 0,startColor, 0,Shader.TileMode.MIRROR);
        mGradient[1] = new LinearGradient(0, 0, 0, ERROR_RECT_WIDTH, startColor, 0, Shader.TileMode.MIRROR);
        mGradient[2] = new LinearGradient(width, 0, width - ERROR_RECT_WIDTH, 0, startColor, 0, Shader.TileMode.MIRROR);
        mGradient[3] = new LinearGradient(0, height, 0, height - ERROR_RECT_WIDTH, startColor, 0, Shader.TileMode.MIRROR);
    }

}
