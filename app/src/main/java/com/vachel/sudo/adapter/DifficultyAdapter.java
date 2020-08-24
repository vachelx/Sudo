package com.vachel.sudo.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;


/**
 * Created by jianglixuan on 2020/8/24.
 * Describe:
 */
public class DifficultyAdapter extends PagerAdapter {
    private ArrayList<String> mData;
    public DifficultyAdapter(){
        mData = new ArrayList<>();
        mData.add("简单");
        mData.add("中等");
        mData.add("困难");
        mData.add("专家");
    }

    public int getDataCount(){
        return mData.size();
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        position %= mData.size();
        if (position < 0) {
            position = mData.size() + position;
        }
        String text = mData.get(position);
        TextView textView = new TextView(container.getContext());
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        container.addView(textView);
        return textView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mData.get(position);
    }

    @Override
    public float getPageWidth(int position) {
        return 1f/3;
    }

}
