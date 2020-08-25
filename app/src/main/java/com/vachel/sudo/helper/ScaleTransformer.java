package com.vachel.sudo.helper;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.vachel.sudo.MyApplication;
import com.vachel.sudo.R;


/**
 * Created by jianglixuan on 2020/8/24.
 * Describe:
 */
public class ScaleTransformer implements ViewPager.PageTransformer {
    private float MIN_ALPHA = 0f;
    private float MIN_SCALE = 0.35f;
    private int mSelectColor;
    private int mUnSelectColor;

    public ScaleTransformer() {
        Resources resources = MyApplication.getInstance().getResources();
        mSelectColor = resources.getColor(R.color.main_blue);
        mUnSelectColor = resources.getColor(R.color.default_text_color);
    }

    @Override
    public void transformPage(@NonNull View page, float position) {
        if (position < -0.33 || position > 1) {
            page.setAlpha(MIN_ALPHA);
            page.setScaleX(MIN_SCALE);
            page.setScaleY(MIN_SCALE);
        } else {
            float factor = Math.abs(position - 0.33f)/0.67f;
            float scaleFactor = 1 - (1 - MIN_SCALE) * factor;
            if (page instanceof TextView) {
                ((TextView) page).setTextColor(scaleFactor > 0.95 ? mSelectColor : mUnSelectColor);
                ((TextView) page).setTypeface(Typeface.defaultFromStyle(scaleFactor > 0.95 ? Typeface.BOLD : Typeface.NORMAL));
            }
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setAlpha(1 - (1 - MIN_ALPHA) * factor);
        }
    }
}
