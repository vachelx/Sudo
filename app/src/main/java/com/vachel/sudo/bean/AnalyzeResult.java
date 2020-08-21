package com.vachel.sudo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jianglixuan on 2020/8/21.
 * Describe:
 */
public class AnalyzeResult implements Parcelable {
    public AnalyzeResult(){

    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getFastTake() {
        return fastTake;
    }

    public void setFastTake(long fastTake) {
        this.fastTake = fastTake;
    }

    public long getAveTake() {
        return aveTake;
    }

    public void setAveTake(long aveTake) {
        this.aveTake = aveTake;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    private boolean success;
    private long fastTake;
    private long aveTake;
    private long lastTime;

    protected AnalyzeResult(Parcel in) {
        success = in.readByte() != 0;
        fastTake = in.readLong();
        aveTake = in.readLong();
        lastTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeLong(fastTake);
        dest.writeLong(aveTake);
        dest.writeLong(lastTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AnalyzeResult> CREATOR = new Creator<AnalyzeResult>() {
        @Override
        public AnalyzeResult createFromParcel(Parcel in) {
            return new AnalyzeResult(in);
        }

        @Override
        public AnalyzeResult[] newArray(int size) {
            return new AnalyzeResult[size];
        }
    };
}
