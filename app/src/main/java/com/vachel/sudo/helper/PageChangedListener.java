package com.vachel.sudo.helper;

import androidx.viewpager.widget.ViewPager;

public abstract class PageChangedListener implements ViewPager.OnPageChangeListener {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        onSelected(position + 1);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public abstract void onSelected(int position);
}
