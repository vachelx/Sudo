package com.vachel.sudo.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.StringRes;

public class ToastUtil extends Toast {

    private ToastUtil(Context context) {
        super(context);
    }

    public static void showShortToast(Context context, CharSequence text) {
        makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showShortToast(Context context, @StringRes int resId) {
        makeText(context, resId, Toast.LENGTH_SHORT).show();
    }


    public static void showLongToast(Context context, CharSequence text) {
        makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void showLongToast(Context context, @StringRes int resId) {
        makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    public static Toast makeText(Context context, CharSequence text, int duration) {
        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, -80);
        return toast;
    }

    public static Toast makeText(Context context, @StringRes int resId, int duration) {
        String text = context.getResources().getString(resId);
        return makeText(context, text, duration);
    }
}
