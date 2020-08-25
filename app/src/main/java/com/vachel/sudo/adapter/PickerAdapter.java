package com.vachel.sudo.adapter;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.vachel.sudo.manager.ThemeManager;

import java.util.ArrayList;

import static java.security.AccessController.getContext;


/**
 * Created by jianglixuan on 2020/8/24.
 * Describe:
 */
public class PickerAdapter extends PagerAdapter {
    private ArrayList<String> mData;
    IItemClickListener mListener;

    public PickerAdapter(@NonNull ArrayList<String> data, @NonNull IItemClickListener listener) {
        mData = data;
        mListener = listener;
    }

    public int getDataCount() {
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
        int realPosition = position % mData.size();
        if (realPosition < 0) {
            realPosition = mData.size() + realPosition;
        }
        String text = mData.get(realPosition);
        TextView textView = new TextView(container.getContext());
        textView.setOnClickListener(v -> mListener.onItemClick(position - 1));
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ThemeManager.getInstance().getLargeTextSize());
        textView.setTextColor(ThemeManager.getInstance().getNormalTextColor());
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
        return 1f / 3;
    }

    public interface IItemClickListener {
        void onItemClick(int position);
    }

}
