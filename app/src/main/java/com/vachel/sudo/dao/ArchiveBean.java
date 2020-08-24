package com.vachel.sudo.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by jianglixuan on 2020/8/24.
 * Describe:
 */

@Entity
public class ArchiveBean {
    @Id
    private String key;

    private String exam;

    private String tmp;

    @Property(nameInDb = "TAKE_TIME")
    private long takeTime;

    @Property(nameInDb = "SAVE_DATE")
    private long saveDate;

    private int mode;

    private int difficulty;

    private int index;

    @Generated(hash = 948365679)
    public ArchiveBean(String key, String exam, String tmp, long takeTime,
            long saveDate, int mode, int difficulty, int index) {
        this.key = key;
        this.exam = exam;
        this.tmp = tmp;
        this.takeTime = takeTime;
        this.saveDate = saveDate;
        this.mode = mode;
        this.difficulty = difficulty;
        this.index = index;
    }

    @Generated(hash = 326361610)
    public ArchiveBean() {
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getExam() {
        return this.exam;
    }

    public void setExam(String exam) {
        this.exam = exam;
    }

    public String getTmp() {
        return this.tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public long getTakeTime() {
        return this.takeTime;
    }

    public void setTakeTime(long takeTime) {
        this.takeTime = takeTime;
    }

    public long getSaveDate() {
        return this.saveDate;
    }

    public void setSaveDate(long saveDate) {
        this.saveDate = saveDate;
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getDifficulty() {
        return this.difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
