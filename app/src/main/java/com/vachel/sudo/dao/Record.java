package com.vachel.sudo.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by jianglixuan on 2020/8/21.
 * Describe:
 */
@Entity
public class Record {
    @Unique
    @Id
    private String key;

    @Property(nameInDb = "TAKE_TIME")
    private long takeTime;
    @Property(nameInDb = "FINISH_DATE")
    private long finishDate;
    private int difficulty;
    private int mode;
    @Generated(hash = 1765799309)
    public Record(String key, long takeTime, long finishDate, int difficulty,
            int mode) {
        this.key = key;
        this.takeTime = takeTime;
        this.finishDate = finishDate;
        this.difficulty = difficulty;
        this.mode = mode;
    }
    @Generated(hash = 477726293)
    public Record() {
    }
    public String getKey() {
        return this.key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public long getTakeTime() {
        return this.takeTime;
    }
    public void setTakeTime(long takeTime) {
        this.takeTime = takeTime;
    }
    public long getFinishDate() {
        return this.finishDate;
    }
    public void setFinishDate(long finishDate) {
        this.finishDate = finishDate;
    }
    public int getDifficulty() {
        return this.difficulty;
    }
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    public int getMode() {
        return this.mode;
    }
    public void setMode(int mode) {
        this.mode = mode;
    }

}
