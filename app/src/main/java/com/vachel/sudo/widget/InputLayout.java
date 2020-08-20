package com.vachel.sudo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.vachel.sudo.R;

import java.util.HashMap;

public class InputLayout extends LinearLayout implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private int[] mTextIds = new int[]{
            R.id.text_0,
            R.id.text_1,
            R.id.text_2,
            R.id.text_3,
            R.id.text_4,
            R.id.text_5,
            R.id.text_6,
            R.id.text_7,
            R.id.text_8,
            R.id.text_9
    };
    private HashMap<Integer, Integer> mList = new HashMap<>();
    private IOnTextClickListener mListener;

    public InputLayout(Context context) {
        this(context, null);
    }

    public InputLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.input_layout, this, true);
        for (int i = 0; i < mTextIds.length; i++) {
            mList.put(mTextIds[i], i);
            rootView.findViewById(mTextIds[i]).setOnClickListener(this);
        }
        ((CheckBox)rootView.findViewById(R.id.mark)).setOnCheckedChangeListener(this);
        rootView.findViewById(R.id.reset).setOnClickListener(this);
        rootView.findViewById(R.id.last_step).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mListener == null) {
            return;
        }
        int id = v.getId();
        if (id == R.id.reset) {
            mListener.onReset();
        } else if (mList.containsKey(id)) {
            Integer value = mList.get(id);
            mListener.onTextClick(value);
        } else if (id == R.id.last_step){
            mListener.onPreStep();
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mListener.onMark(isChecked);
    }

    interface IOnTextClickListener {
        void onTextClick(Integer value);

        void onReset();

        void onMark(boolean mark);

        void onPreStep();
    }

    public void setOnTextClickListener(IOnTextClickListener listener) {
        mListener = listener;
    }
}
