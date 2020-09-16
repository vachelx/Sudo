package com.vachel.sudo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class SlideBean implements Parcelable {
    private String title;
    private ArrayList<SlideItem> slideItems;
    public SlideBean(){

    }

    protected SlideBean(Parcel in) {
        title = in.readString();
        slideItems = in.createTypedArrayList(SlideItem.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeTypedList(slideItems);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SlideBean> CREATOR = new Creator<SlideBean>() {
        @Override
        public SlideBean createFromParcel(Parcel in) {
            return new SlideBean(in);
        }

        @Override
        public SlideBean[] newArray(int size) {
            return new SlideBean[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<SlideItem> getSlideItems() {
        return slideItems;
    }

    public void setSlideItems(ArrayList<SlideItem> slideItems) {
        this.slideItems = slideItems;
    }
}
