package com.vachel.sudo;

import android.app.Application;

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
        ThreadPoolX.getThreadPool().execute(() -> {
            DatabaseManager.initGreenDao(sInstance);
            DatabaseManager.initCreateAllExams();
        });
    }

    public static Application getInstance(){
        return sInstance;
    }
}
