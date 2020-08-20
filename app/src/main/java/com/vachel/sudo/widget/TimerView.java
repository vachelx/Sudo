package com.vachel.sudo.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;


/**
 * Created by jianglixuan on 2020/8/20.
 * Describe:
 */
public class TimerView extends TextView {

    private Handler mHandler;
    private long mStartTime;
    private long mOffsetTime;

    public TimerView(Context context) {
        this(context, null);
    }

    public TimerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mHandler = new TimerHandler(this);
    }

    public void startTimer(){
        mStartTime = System.currentTimeMillis();
        mHandler.sendEmptyMessage(0);
    }

    public void pauseTimer() {
        mOffsetTime += System.currentTimeMillis() - mStartTime;
        stopTimer();
    }

    public void stopTimer() {
        mHandler.removeCallbacksAndMessages(null);
    }

    private void updateTime() {
        long currentTime = System.currentTimeMillis();
        long offsetTime = (currentTime - mStartTime + mOffsetTime) / 1000;
        int hour = (int) (offsetTime / 60 / 60);
        int minute = (int) (offsetTime / 60 % 60);
        int second = (int) (offsetTime % 60);
        StringBuilder text = new StringBuilder();
        if (hour > 0) {
            text.append(hour).append(":");
        }
        if (minute < 10) {
            text.append(0);
        }
        text.append(minute).append(":");
        if (second < 10) {
            text.append(0);
        }
        text.append(second);
        setText(text.toString());
    }

    static class TimerHandler extends Handler {
        private WeakReference<TimerView> reference;

        TimerHandler(TimerView view) {
            reference = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            TimerView timerView = reference.get();
            if (timerView == null){
                return;
            }
            timerView.updateTime();
            sendEmptyMessageDelayed(0, 1000);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopTimer();
    }
}
