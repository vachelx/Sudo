package com.vachel.sudo.manager;

import android.content.res.Resources;

import com.vachel.sudo.MyApplication;
import com.vachel.sudo.R;

/**
 * Created by jianglixuan on 2020/8/25.
 * Describe:
 */
public class ThemeManager {
    private volatile static ThemeManager mInstance;
    private int mMainColor;
    private final Resources mResources;
    private int mNormalTextColor;
    private int mDefaultTextSize;
    private int mLargeTextSize;

    private ThemeManager() {
        mResources = MyApplication.getInstance().getResources();
    }

    public static ThemeManager getInstance() {
        if (mInstance == null) {
            synchronized (ThemeManager.class) {
                if (mInstance == null) {
                    mInstance = new ThemeManager();
                }
            }
        }
        return mInstance;
    }

    public int getDefaultThemeColor() {
        if (mMainColor == 0) {
            mMainColor = mResources.getColor(R.color.main_blue);
        }
        return mMainColor;
    }

    public int getNormalTextColor() {
        if (mNormalTextColor == 0) {
            mNormalTextColor = mResources.getColor(R.color.default_text_color);
        }
        return mNormalTextColor;
    }

    public int getDefaultTextSize() {
        if (mDefaultTextSize == 0) {
            mDefaultTextSize = mResources.getDimensionPixelSize(R.dimen.default_text_size);
        }
        return mDefaultTextSize;
    }

    public int getLargeTextSize() {
        if (mLargeTextSize == 0) {
            mLargeTextSize = mResources.getDimensionPixelSize(R.dimen.large_text_size);
        }
        return mLargeTextSize;
    }
}
