package com.hu.easycall.service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.Nullable;

import com.hu.easycall.util.LogUtil;
import com.hu.easycall.util.NotificationUtil;

import java.lang.ref.WeakReference;

public abstract class BaseService extends Service {
    public static final String STOP_ACTION = "STOP_CX_SERVICE";
    private boolean isCancelStopService = false;
    private CXServiceHandler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1, NotificationUtil.createServiceForegroundNotification(this));
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (checkIntent(intent)) {
            onStartCommand(intent);
        }
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public abstract void onStartCommand(Intent intent);

    public boolean checkIntent(Intent intent) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(STOP_ACTION)) {
                if (mHandler == null) {
                    mHandler = new CXServiceHandler(this);
                }
                isCancelStopService = false;
                mHandler.sendMessageDelayed(mHandler.obtainMessage(0), 500);
                return false;
            } else {
                isCancelStopService = true;
            }
        }
        return true;
    }

    private static class CXServiceHandler extends Handler {
        WeakReference<? extends BaseService> mReference;

        CXServiceHandler(BaseService service) {
            mReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            final BaseService service = mReference.get();
            if (service == null || service.isCancelStopService) {
                return;
            }
            service.stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("jlx", "service onDestory :" + this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            stopForeground(true);
        }
    }
}
