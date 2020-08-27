package com.vachel.sudo.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vachel.sudo.utils.Utils;

import java.lang.ref.WeakReference;


/**
 * Created by jianglixuan on 2020/8/20.
 * Describe:
 */
public class TimerView extends TextView {

    private Handler mHandler;
    private long mStartTime;
    private long mOffsetTime;
    private long mRealTakeTime;
    private int mState; // 0初始态, 1开始计时， 2暂停， -1停止

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

    public long getTakeTime() {
        return mRealTakeTime;
    }

    public void startTimerWithMillis(long offsetTime) {
        mHandler.removeCallbacksAndMessages(null);
        mOffsetTime = offsetTime;
        mStartTime = System.currentTimeMillis();
        mHandler.sendEmptyMessage(0);
        mState = 1;
    }

    public void startTimer() {
        if (mState == 0 || mState == -1) {
            mOffsetTime = 0;
            mStartTime = System.currentTimeMillis();
            mHandler.sendEmptyMessage(0);
        } else if (mState == 2) {
            mStartTime = System.currentTimeMillis();
            mHandler.sendEmptyMessage(0);
        }
        mState = 1;
    }

    // 返回
    public void pauseTimer() {
        if (mState == 1) {
            mHandler.removeCallbacksAndMessages(null);
            mOffsetTime += System.currentTimeMillis() - mStartTime;
            mState = 2;
        }
    }

    public long stopTimer() {
        if (mState == 1) {
            mHandler.removeCallbacksAndMessages(null);
            mOffsetTime += System.currentTimeMillis() - mStartTime;
            mState = -1;
        }
        return mOffsetTime;
    }

    private void updateTime() {
        long currentTime = System.currentTimeMillis();
        mRealTakeTime = currentTime - mStartTime + mOffsetTime;
        setText(Utils.parseTakeTime(mRealTakeTime, 0));
    }

    public void onResetStart() {
        startTimerWithMillis(0);
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
