package com.vachel.sudo.manager;

import com.vachel.sudo.dao.ArchiveBean;
import com.vachel.sudo.dao.ArchiveBeanDao;
import com.vachel.sudo.dao.DatabaseManager;

import org.simple.eventbus.EventBus;

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

    public void deleteArchive(String key) {
        DatabaseManager.getArchiveBeanDao().deleteByKey(key);
    }

    public List<ArchiveBean> getAllArchives() {
        return DatabaseManager.getArchiveBeanDao().loadAll();
    }

    public ArchiveBean getArchiveByDiff(int mode, int difficulty) {
        return DatabaseManager.getArchiveBeanDao().queryBuilder().where(ArchiveBeanDao.Properties.Mode.eq(mode), ArchiveBeanDao.Properties.Difficulty.eq(difficulty)).build().unique();
    }

    // 检查随机模式的difficulty下有无存档
    public boolean checkRandomArchiveExist(int difficulty) {
        List<ArchiveBean> list = DatabaseManager.getArchiveBeanDao().queryBuilder().where(ArchiveBeanDao.Properties.Mode.eq(0), ArchiveBeanDao.Properties.Difficulty.eq(difficulty)).build().list();
        return list != null && list.size() > 0;
    }

    // 用于解锁模式判断有无存档
    public boolean checkArchiveExist(String key) {
        return getArchive(key) != null;
    }
}
