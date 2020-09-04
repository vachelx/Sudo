package com.vachel.sudo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.vachel.sudo.R;
import com.vachel.sudo.manager.ThemeManager;


/**
 * Created by jianglixuan on 2020/8/27.
 * Describe:
 */
public class RecordItem extends LinearLayout {

    private String mFirstText;
    private String mSecondText;
    private TextView mItemValue;

    public RecordItem(Context context) {
        this(context, null);
    }

    public RecordItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.vachel);
        mFirstText = a.getString(R.styleable.vachel_first_text);
        mSecondText = a.getString(R.styleable.vachel_second_text);
        a.recycle();
        TextView itemName = new TextView(context);
        itemName.setTextColor(getResources().getColor(R.color.default_grey_text_color));
        itemName.setTypeface(itemName.getTypeface(), Typeface.ITALIC);
        itemName.setTextSize(TypedValue.COMPLEX_UNIT_PX, ThemeManager.getInstance().getDefaultTextSize());
        itemName.setText(mFirstText);
        itemName.setWidth(getResources().getDimensionPixelSize(R.dimen.record_item_name_width));
        addView(itemName);

        mItemValue = new TextView(context);
        mItemValue.setText(mSecondText);
        mItemValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, ThemeManager.getInstance().getDefaultTextSize());
        mItemValue.setTextColor(getResources().getColor(R.color.default_text_color));
        addView(mItemValue);
    }

    public void setItemValue(CharSequence text) {
        mItemValue.setText(text);
    }
}
