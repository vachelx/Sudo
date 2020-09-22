package com.vachel.sudo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.vachel.sudo.R;

import java.util.ArrayList;
import java.util.HashMap;

public class InputLayout extends LinearLayout implements View.OnClickListener {
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

    private ArrayList<View> canLockViews = new ArrayList<>();

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
            View view = rootView.findViewById(mTextIds[i]);
            view.setOnClickListener(this);
            canLockViews.add(view);
        }
        View markView = rootView.findViewById(R.id.mark);
        markView.setOnClickListener(this);
        canLockViews.add(markView);
        rootView.findViewById(R.id.reset).setOnClickListener(this);
        View lastStep = rootView.findViewById(R.id.last_step);
        lastStep.setOnClickListener(this);
        canLockViews.add(lastStep);

        View saveView = rootView.findViewById(R.id.save);
        saveView.setOnClickListener(this);
        canLockViews.add(saveView);
    }

    @Override
    public void onClick(View v) {
        if (mListener == null) {
            return;
        }
        int id = v.getId();
        if (id == R.id.reset) {
            mListener.onResetClick();
        } else if (mList.containsKey(id)) {
            Integer value = mList.get(id);
            mListener.onTextClick(value);
        } else if (id == R.id.last_step){
            mListener.onPreStepClick();
        } else if(id == R.id.mark){
            boolean selected = !v.isSelected();
            v.setSelected(selected);
            mListener.onMarkClick(selected);
        } else if (id == R.id.save){
            mListener.onSaveClick();
        }

    }

    public void setLock(boolean lock) {
        for (View view : canLockViews) {
            view.setClickable(!lock);
        }
    }

    interface IOnTextClickListener {
        void onTextClick(Integer value);

        void onResetClick();

        void onMarkClick(boolean mark);

        void onPreStepClick();

        void onSaveClick();
    }

    public void setOnTextClickListener(IOnTextClickListener listener) {
        mListener = listener;
    }

    public void updateKeyNumberUseCounts(Integer[][] sudo) {
        int[] counts = new int[9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Integer value = sudo[i][j];
                if (value != 0) {
                    counts[value - 1] += 1;
                }
            }
        }

        for (View view : canLockViews) {
            if (view instanceof KeyTextView && mList.containsKey(view.getId())) {
                Integer value = mList.get(view.getId());
                ((KeyTextView) view).setCountText((9 - counts[value - 1]) + "");
            }
        }

    }
}
