package com.vachel.sudo;

import android.app.Application;
import android.content.Context;

import com.tencent.bugly.Bugly;
import com.vachel.sudo.dao.DatabaseManager;
import com.vachel.sudo.engine.ThreadPoolX;


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
        ThreadPoolX.getThreadPool().execute(() -> {
            DatabaseManager.initGreenDao(sInstance);
            DatabaseManager.initCreateAllExams();
        });
    }

    public static Application getInstance(){
        return sInstance;
    }

    public static Context getContext() {
        return sInstance.getApplicationContext();
    }
}
