package com.vachel.sudo.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.vachel.sudo.MyApplication;
import com.vachel.sudo.manager.ExamDataManager;
import com.vachel.sudo.utils.Arithmetic;
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

    public static void initCreateAllExams() {
        if (PreferencesUtils.getBooleanPreference(MyApplication.getInstance(), "has_create_all_exams", false)) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            int count = i == 0 ? 36 : i == 1 ? 48 : i == 2 ? 60 : 72;
            for (int j = 0; j < count; j++) {
                Integer[][] sudo = Arithmetic.getExamSudo(i);
                String examKey = Utils.getExamKey(1, i, 0, j);
                Examination examination = new Examination(examKey, Utils.sudoToString(sudo), i, j, 3,
                        0, 0, 1, 0);
                ExamDataManager.getInstance().addOrUpdateExamination(examination);
            }
        }
        PreferencesUtils.setBooleanPreference(MyApplication.getInstance(), "has_create_all_exams", true);
    }
}
