package com.vachel.sudo.utils.transformer;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;


/**
 * Created by jianglixuan on 2020/8/24.
 * Describe:
 */
public class ScaleTransformer implements ViewPager.PageTransformer {
    private float MIN_ALPHA = 0f;
    private float MIN_SCALE = 0.3f;

    @Override
    public void transformPage(@NonNull View page, float position) {
        if (position < -0.33 || position > 1) {
            page.setAlpha(MIN_ALPHA);
            page.setScaleX(MIN_SCALE);
            page.setScaleY(MIN_SCALE);
        } else {
            float factor = Math.abs(position - 0.33f);
            page.setScaleX(1 - (1 - MIN_SCALE) * factor);
            page.setScaleY(1 - (1 - MIN_SCALE) * factor);
            page.setAlpha(1 - (1 - MIN_ALPHA) * factor);
        }
    }
}
