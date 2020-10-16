package com.vachel.sudo.service;

import android.content.Intent;
import android.util.Log;

import com.vachel.sudo.engine.ThreadPoolX;
import com.vachel.sudo.utils.Utils;

public class ExamsCreateService extends BaseService {
    private int mCreateCount = 0;

    @Override
    public void onStartCommand(Intent intent) {
        ThreadPoolX.getThreadPool().execute(() -> Utils.initCreateAllExams(count -> {
            mCreateCount++;
            if (mCreateCount == 999 * 4) {
                Intent stopIntent = new Intent();
                stopIntent.setAction(STOP_ACTION);
                checkIntent(stopIntent);
                Log.d("jlx", "STOP_ACTION mCreateCount = " + mCreateCount);
            }
        }));
    }

    public interface ICreateExamsCallback {
        void onCreateIndex(int count);
    }
}
