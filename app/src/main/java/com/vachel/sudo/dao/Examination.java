package com.vachel.sudo.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by jianglixuan on 2020/8/21.
 * Describe:
 */

@Entity
public class Examination {
    @Unique
    @Id
    private String key;

    @NotNull
    private String exam;

    private int difficulty;

    @Property(nameInDb = "SORT_INDEX")
    private int sortIndex;

    @Property(nameInDb = "TIPS_COUNT")
    private int tipsCount;

    @Property(nameInDb = "TAKE_TIME")
    private long takeTime;

    @Property(nameInDb = "FINISH_DATE")
    private long finishDate;

    private int mode;

    private int version;

    @Generated(hash = 1837250576)
    public Examination(String key, @NotNull String exam, int difficulty,
            int sortIndex, int tipsCount, long takeTime, long finishDate, int mode,
            int version) {
        this.key = key;
        this.exam = exam;
        this.difficulty = difficulty;
        this.sortIndex = sortIndex;
        this.tipsCount = tipsCount;
        this.takeTime = takeTime;
        this.finishDate = finishDate;
        this.mode = mode;
        this.version = version;
    }

    @Generated(hash = 1518633012)
    public Examination() {
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

    public int getDifficulty() {
        return this.difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getSortIndex() {
        return this.sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    public int getTipsCount() {
        return this.tipsCount;
    }

    public void setTipsCount(int tipsCount) {
        this.tipsCount = tipsCount;
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

    public int getMode() {
        return this.mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
