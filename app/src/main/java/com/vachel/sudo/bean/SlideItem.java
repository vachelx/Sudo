package com.vachel.sudo.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class SlideItem implements Parcelable {
    private String itemText;
    private boolean isChecked;

    public SlideItem(){

    }

    protected SlideItem(Parcel in) {
        itemText = in.readString();
        isChecked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemText);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SlideItem> CREATOR = new Creator<SlideItem>() {
        @Override
        public SlideItem createFromParcel(Parcel in) {
            return new SlideItem(in);
        }

        @Override
        public SlideItem[] newArray(int size) {
            return new SlideItem[size];
        }
    };

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
