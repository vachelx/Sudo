package com.vachel.sudo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.TreeSet;

/**
 * Created by jianglixuan on 2020/8/20.
 * Describe:
 */
public class CellHistoryBean implements Parcelable {
    private int x;
    private int y;
    private Integer value = 0;
    private TreeSet<Integer> markValue;

    public CellHistoryBean(int x, int y, Integer value){
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public CellHistoryBean(int x, int y, TreeSet<Integer> marks){
        this.x = x;
        this.y = y;
        this.markValue = marks;
    }

    protected CellHistoryBean(Parcel in) {
        x = in.readInt();
        y = in.readInt();
        if (in.readByte() == 0) {
            value = null;
        } else {
            value = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(x);
        dest.writeInt(y);
        if (value == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(value);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CellHistoryBean> CREATOR = new Creator<CellHistoryBean>() {
        @Override
        public CellHistoryBean createFromParcel(Parcel in) {
            return new CellHistoryBean(in);
        }

        @Override
        public CellHistoryBean[] newArray(int size) {
            return new CellHistoryBean[size];
        }
    };

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public TreeSet<Integer> getMarkValue() {
        return markValue;
    }

    public void setMarkValue(TreeSet<Integer> markValue) {
        this.markValue = markValue;
    }

    @NonNull
    @Override
    public String toString() {
        return "x = " + x + "; y = " + y + "; value = " + value + "; markValue = " + markValue;
    }
}
