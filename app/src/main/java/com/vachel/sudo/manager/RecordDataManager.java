package com.vachel.sudo.manager;

import com.vachel.sudo.dao.DatabaseManager;
import com.vachel.sudo.dao.Record;
import com.vachel.sudo.dao.RecordDao;

import java.util.List;

/**
 * Created by jianglixuan on 2020/8/21.
 * Describe:
 */
public class RecordDataManager {
    private volatile static RecordDataManager mInstance;

    public static RecordDataManager getInstance() {
        if (mInstance == null) {
            synchronized (RecordDataManager.class) {
                if (mInstance == null) {
                    mInstance = new RecordDataManager();
                }
            }
        }
        return mInstance;
    }


    //插入/更新通关记录
    public void addOrUpdateRecord(Record record) {
        DatabaseManager.getRecordDao().insertOrReplace(record);
        DatabaseManager.getRecordDao().detachAll();
    }

    public void addOrUpdateRecords(List<Record> beans) {
        DatabaseManager.getRecordDao().insertOrReplaceInTx(beans);
        DatabaseManager.getRecordDao().detachAll();
    }

    public Record getRecord(String key) {
        return DatabaseManager.getRecordDao().queryBuilder().where(RecordDao.Properties.Key.eq(key)).build().unique();
    }

    public List<Record> getAllRecords() {
        return DatabaseManager.getRecordDao().loadAll();
    }

    public int getRecordSizeByClassify(int mode, int difficulty) {
        List<Record> records = DatabaseManager.getRecordDao().queryBuilder().where(RecordDao.Properties.Mode.eq(mode), RecordDao.Properties.Difficulty.eq(difficulty)).build().list();
        return records.size();
    }

    public List<Record> getFilterRecords(int mode, int difficulty) {
        return DatabaseManager.getRecordDao().queryBuilder().where(RecordDao.Properties.Mode.eq(mode), RecordDao.Properties.Difficulty.eq(difficulty)).build().list();
    }

    public List<Record> getFilterRecordsByDifficulty(int difficulty) {
        return DatabaseManager.getRecordDao().queryBuilder().where(RecordDao.Properties.Difficulty.eq(difficulty)).build().list();
    }

    public List<Record> getFilterRecordsByMode(int mode) {
        return DatabaseManager.getRecordDao().queryBuilder().where(RecordDao.Properties.Mode.eq(mode)).build().list();
    }

}
