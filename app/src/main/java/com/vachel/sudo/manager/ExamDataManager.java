package com.vachel.sudo.manager;

import com.vachel.sudo.dao.DatabaseManager;
import com.vachel.sudo.dao.Examination;
import com.vachel.sudo.dao.ExaminationDao;

import java.util.List;

/**
 * Created by jianglixuan on 2020/8/21.
 * Describe:
 */
public class ExamDataManager {
    private volatile static ExamDataManager mInstance;

    public static ExamDataManager getInstance() {
        if (mInstance == null) {
            synchronized (ExamDataManager.class) {
                if (mInstance == null) {
                    mInstance = new ExamDataManager();
                }
            }
        }
        return mInstance;
    }

    public void addOrUpdateExamination(Examination exam) {
        DatabaseManager.getExaminationDao().insertOrReplace(exam);
        DatabaseManager.getExaminationDao().detachAll();
    }

    public void addOrUpdateExaminations(List<Examination> beans) {
        DatabaseManager.getExaminationDao().insertOrReplaceInTx(beans);
        DatabaseManager.getExaminationDao().detachAll();
    }

    public Examination getExam(String key) {
        return DatabaseManager.getExaminationDao().queryBuilder().where(ExaminationDao.Properties.Key.eq(key)).build().unique();
    }

    public List<Examination> getAllExams() {
        return DatabaseManager.getExaminationDao().loadAll();
    }

    public List<Examination> getExamsByDiff(int mode, int difficulty) {
        return DatabaseManager.getExaminationDao().queryBuilder().where(ExaminationDao.Properties.Mode.eq(mode), ExaminationDao.Properties.Difficulty.eq(difficulty)).build().list();
    }

}
