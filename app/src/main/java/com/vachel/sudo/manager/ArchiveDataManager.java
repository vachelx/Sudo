package com.vachel.sudo.manager;

import com.vachel.sudo.dao.ArchiveBean;
import com.vachel.sudo.dao.ArchiveBeanDao;
import com.vachel.sudo.dao.DatabaseManager;

import java.util.List;

/**
 * Created by jianglixuan on 2020/8/24.
 * Describe:
 */
public class ArchiveDataManager {
    private volatile static ArchiveDataManager mInstance;

    public static ArchiveDataManager getInstance() {
        if (mInstance == null) {
            synchronized (ArchiveDataManager.class) {
                if (mInstance == null) {
                    mInstance = new ArchiveDataManager();
                }
            }
        }
        return mInstance;
    }

    public void addOrUpdateArchive(ArchiveBean bean) {
        DatabaseManager.getArchiveBeanDao().insertOrReplace(bean);
        DatabaseManager.getArchiveBeanDao().detachAll();
    }

    public void addOrUpdateArchives(List<ArchiveBean> beans) {
        DatabaseManager.getArchiveBeanDao().insertOrReplaceInTx(beans);
        DatabaseManager.getArchiveBeanDao().detachAll();
    }

    public ArchiveBean getArchive(String key) {
        return DatabaseManager.getArchiveBeanDao().queryBuilder().where(ArchiveBeanDao.Properties.Key.eq(key)).build().unique();
    }

    public List<ArchiveBean> getAllArchives() {
        return DatabaseManager.getArchiveBeanDao().loadAll();
    }

    public List<ArchiveBean> getArchivesByDiff(int mode, int difficulty, int index) {
        if (mode == 0){
            return DatabaseManager.getArchiveBeanDao().queryBuilder().where(ArchiveBeanDao.Properties.Mode.eq(mode), ArchiveBeanDao.Properties.Difficulty.eq(difficulty)).build().list();
        } else {
            return DatabaseManager.getArchiveBeanDao().queryBuilder().where(ArchiveBeanDao.Properties.Mode.eq(mode), ArchiveBeanDao.Properties.Difficulty.eq(difficulty), ArchiveBeanDao.Properties.Index.eq(index)).build().list();
        }
    }
}
