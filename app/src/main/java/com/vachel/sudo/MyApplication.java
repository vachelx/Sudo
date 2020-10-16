package com.vachel.sudo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.tencent.bugly.Bugly;
import com.vachel.sudo.service.ExamsCreateService;
import com.vachel.sudo.dao.DatabaseManager;
import com.vachel.sudo.utils.Constants;
import com.vachel.sudo.utils.NotificationUtil;
import com.vachel.sudo.utils.PreferencesUtils;


/**
 * Created by jianglixuan on 2020/8/21.
 * Describe:
 */
public class MyApplication extends Application {
    private static Application sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Bugly.init(getApplicationContext(), "5861c6d2c5", true);
        DatabaseManager.initGreenDao(sInstance);
        initCreateAllExams();
    }

    private void initCreateAllExams() {
        if (PreferencesUtils.getBooleanPreference(MyApplication.getInstance(), Constants.HAS_CREATE_ALL_EXAMS, false)) {
            return;
        }
        NotificationUtil.createCommonNotificationChannel();
        Intent intent = new Intent(this, ExamsCreateService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    public static Application getInstance(){
        return sInstance;
    }

    public static Context getContext() {
        return sInstance.getApplicationContext();
    }
}
