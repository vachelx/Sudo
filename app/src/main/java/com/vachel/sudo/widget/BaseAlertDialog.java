package com.vachel.sudo.widget;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.vachel.sudo.MyApplication;
import com.vachel.sudo.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BaseAlertDialog extends DialogFragment {

    private IDialogListener mListener = null;
    private CharSequence mPositiveText = null;
    private CharSequence mNegativeText = null;
    private boolean mIsCancelAble = true;
    private boolean mIsAutoDismissAble = true;
    private boolean showCheckBox;
    private DialogInterface.OnKeyListener mOnKeyListener;
    private CompoundButton.OnCheckedChangeListener checkBoxChangeListener;
    private String mCheckBoxText;

    public BaseAlertDialog() {
    }

    public BaseAlertDialog initDialog(String title, String message) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        setArguments(args);
        return this;
    }

    public BaseAlertDialog setPositiveTextDefault(){
        mPositiveText = MyApplication.getContext().getString(R.string.common_sure);
        return this;
    }

    public BaseAlertDialog setPositiveText(CharSequence positiveText) {
        mPositiveText = positiveText;
        return this;
    }

    public BaseAlertDialog setNegativeTextDefault(){
        mNegativeText = MyApplication.getContext().getString(R.string.common_cancel);
        return this;
    }

    public BaseAlertDialog setNegativeText(CharSequence negativeText) {
        mNegativeText = negativeText;
        return this;
    }

    public BaseAlertDialog setCancelAble(boolean isCancelAble) {
        mIsCancelAble = isCancelAble;
        return this;
    }

    public BaseAlertDialog setListener(IDialogListener listener) {
        mListener = listener;
        return this;
    }

    public BaseAlertDialog setOnKeyListener(DialogInterface.OnKeyListener listener) {
        mOnKeyListener = listener;
        return this;
    }

    public BaseAlertDialog setAutoDismissAble(boolean autoDismissAble) {
        mIsAutoDismissAble = autoDismissAble;
        return this;
    }

    /**
     * 设置显示勾选切换按钮和按钮变换监听 第一个参数为true 第二个不能为空
     * @param onCheckedChangeListener
     */
    public BaseAlertDialog setShowCheckBox(String checkBoxText,CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.showCheckBox = true;
        mCheckBoxText = checkBoxText;
        this.checkBoxChangeListener = onCheckedChangeListener;
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return baseAlertDialog(getArguments().getString("title"), getArguments().getString("message"));
    }

    private Dialog baseAlertDialog(String titleName, String message) {
        final Dialog dialog = new Dialog(getActivity(), R.style.CustomDialog);
        final View.OnClickListener listener = view -> {
            if (mListener == null) {
                dialog.dismiss();
            } else {
                if (view.getId() == R.id.sure) {
                    mListener.onPositiveClick();
                } else if (view.getId() == R.id.cancel) {
                    mListener.onNegativeClick();
                }
            }
            if(mIsAutoDismissAble){
                dialog.dismiss();
            }
        };
        DialogInterface.OnKeyListener onKeyListener = (arg0, arg1, arg2) -> true;

        LayoutInflater mInflater = LayoutInflater.from(this.getActivity());
        View convertView = mInflater.inflate(R.layout.base_dialog_view, null);
        TextView cancel = convertView.findViewById(R.id.cancel);
        TextView sure = convertView.findViewById(R.id.sure);

        TextView title = convertView.findViewById(R.id.title);
        TextView content = convertView.findViewById(R.id.content);
        CheckBox checkBox = convertView.findViewById(R.id.check_box);
        TextView checkContent = convertView.findViewById(R.id.check_content);
        View checkContainer = convertView.findViewById(R.id.check_container);

        if (!TextUtils.isEmpty(titleName)) {
            title.setText(titleName);
            content.setTextColor(getActivity().getResources().getColor(R.color.default_text_color));
        } else {
            title.setVisibility(View.GONE);
            content.setTextColor(getActivity().getResources().getColor(R.color.main_blue));
        }
        if (TextUtils.isEmpty(message)) {
            content.setVisibility(View.GONE);
        } else {
            content.setText(message);
        }

        if (showCheckBox) {
            checkContainer.setVisibility(View.VISIBLE);
            checkBox.setVisibility(View.VISIBLE);
            if (null != checkBoxChangeListener) {
                checkBox.setOnCheckedChangeListener(checkBoxChangeListener);
            }
            checkContent.setText(mCheckBoxText);
        } else {
            checkContainer.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(mNegativeText)) {
            cancel.setVisibility(View.GONE);
        } else {
            cancel.setText(mNegativeText);
        }

        if (TextUtils.isEmpty(mPositiveText)) {
            sure.setVisibility(View.GONE);
        } else {
            sure.setText(mPositiveText);
        }
        dialog.setCanceledOnTouchOutside(mIsCancelAble);
        dialog.setCancelable(mIsCancelAble);
        if (!mIsCancelAble) {
            dialog.setOnKeyListener(onKeyListener);
        }
        if(mOnKeyListener != null){
            dialog.setOnKeyListener(mOnKeyListener);
        }
        sure.setOnClickListener(listener);
        cancel.setOnClickListener(listener);
        dialog.setContentView(convertView);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            getDialog().getWindow().setLayout((int) (dm.widthPixels * 0.75), ViewGroup.LayoutParams.WRAP_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            Method showAllowingStateLoss = this.getClass().getSuperclass().getDeclaredMethod(
                    "showAllowingStateLoss", FragmentManager.class, String.class);
            showAllowingStateLoss.invoke(this, manager, tag);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            try {
                super.show(manager, tag);
            } catch (Exception ignored) {}
        }
    }

    public interface IDialogListener {

        void onPositiveClick();

        void onNegativeClick();

    }
}
