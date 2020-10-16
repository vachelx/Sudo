package com.vachel.sudo.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.vachel.sudo.MyApplication;
import com.vachel.sudo.engine.ThreadPoolX;
import com.vachel.sudo.manager.ExamDataManager;
import com.vachel.sudo.engine.Algorithm;
import com.vachel.sudo.utils.Constants;
import com.vachel.sudo.utils.PreferencesUtils;
import com.vachel.sudo.utils.Utils;

/**
 * Created by jianglixuan on 2020/8/21.
 * Describe:
 */
public class DatabaseManager {
    private static DaoSession mDaoSession;
    private static DaoMaster mDaoMaster;

    public static void initGreenDao(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context,"record.db");
        SQLiteDatabase database = helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(database);
        mDaoSession = mDaoMaster.newSession();
    }

    public static DaoSession getDaoSession(){
        return mDaoSession;
    }

    public static DaoMaster getDaoMaster(){
        return mDaoMaster;
    }

    public static RecordDao getRecordDao(){
        return getDaoSession().getRecordDao();
    }

    public static ExaminationDao getExaminationDao(){
        return getDaoSession().getExaminationDao();
    }

    public static ArchiveBeanDao getArchiveBeanDao(){
        return getDaoSession().getArchiveBeanDao();
    }
}
