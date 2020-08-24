package com.vachel.sudo.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ARCHIVE_BEAN".
*/
public class ArchiveBeanDao extends AbstractDao<ArchiveBean, String> {

    public static final String TABLENAME = "ARCHIVE_BEAN";

    /**
     * Properties of entity ArchiveBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Key = new Property(0, String.class, "key", true, "KEY");
        public final static Property Exam = new Property(1, String.class, "exam", false, "EXAM");
        public final static Property Tmp = new Property(2, String.class, "tmp", false, "TMP");
        public final static Property TakeTime = new Property(3, long.class, "takeTime", false, "TAKE_TIME");
        public final static Property SaveDate = new Property(4, long.class, "saveDate", false, "SAVE_DATE");
        public final static Property Mode = new Property(5, int.class, "mode", false, "MODE");
        public final static Property Difficulty = new Property(6, int.class, "difficulty", false, "DIFFICULTY");
        public final static Property Index = new Property(7, int.class, "index", false, "INDEX");
    }


    public ArchiveBeanDao(DaoConfig config) {
        super(config);
    }
    
    public ArchiveBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ARCHIVE_BEAN\" (" + //
                "\"KEY\" TEXT PRIMARY KEY NOT NULL ," + // 0: key
                "\"EXAM\" TEXT," + // 1: exam
                "\"TMP\" TEXT," + // 2: tmp
                "\"TAKE_TIME\" INTEGER NOT NULL ," + // 3: takeTime
                "\"SAVE_DATE\" INTEGER NOT NULL ," + // 4: saveDate
                "\"MODE\" INTEGER NOT NULL ," + // 5: mode
                "\"DIFFICULTY\" INTEGER NOT NULL ," + // 6: difficulty
                "\"INDEX\" INTEGER NOT NULL );"); // 7: index
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ARCHIVE_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ArchiveBean entity) {
        stmt.clearBindings();
 
        String key = entity.getKey();
        if (key != null) {
            stmt.bindString(1, key);
        }
 
        String exam = entity.getExam();
        if (exam != null) {
            stmt.bindString(2, exam);
        }
 
        String tmp = entity.getTmp();
        if (tmp != null) {
            stmt.bindString(3, tmp);
        }
        stmt.bindLong(4, entity.getTakeTime());
        stmt.bindLong(5, entity.getSaveDate());
        stmt.bindLong(6, entity.getMode());
        stmt.bindLong(7, entity.getDifficulty());
        stmt.bindLong(8, entity.getIndex());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ArchiveBean entity) {
        stmt.clearBindings();
 
        String key = entity.getKey();
        if (key != null) {
            stmt.bindString(1, key);
        }
 
        String exam = entity.getExam();
        if (exam != null) {
            stmt.bindString(2, exam);
        }
 
        String tmp = entity.getTmp();
        if (tmp != null) {
            stmt.bindString(3, tmp);
        }
        stmt.bindLong(4, entity.getTakeTime());
        stmt.bindLong(5, entity.getSaveDate());
        stmt.bindLong(6, entity.getMode());
        stmt.bindLong(7, entity.getDifficulty());
        stmt.bindLong(8, entity.getIndex());
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public ArchiveBean readEntity(Cursor cursor, int offset) {
        ArchiveBean entity = new ArchiveBean( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // key
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // exam
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // tmp
            cursor.getLong(offset + 3), // takeTime
            cursor.getLong(offset + 4), // saveDate
            cursor.getInt(offset + 5), // mode
            cursor.getInt(offset + 6), // difficulty
            cursor.getInt(offset + 7) // index
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ArchiveBean entity, int offset) {
        entity.setKey(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setExam(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTmp(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTakeTime(cursor.getLong(offset + 3));
        entity.setSaveDate(cursor.getLong(offset + 4));
        entity.setMode(cursor.getInt(offset + 5));
        entity.setDifficulty(cursor.getInt(offset + 6));
        entity.setIndex(cursor.getInt(offset + 7));
     }
    
    @Override
    protected final String updateKeyAfterInsert(ArchiveBean entity, long rowId) {
        return entity.getKey();
    }
    
    @Override
    public String getKey(ArchiveBean entity) {
        if(entity != null) {
            return entity.getKey();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ArchiveBean entity) {
        return entity.getKey() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}