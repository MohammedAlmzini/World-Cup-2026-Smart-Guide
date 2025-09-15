package com.ahmmedalmzini783.wcguide.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AdditionalImage implements Parcelable {
    private String url;
    private String caption;
    private int order;

    // Default constructor for Firebase
    public AdditionalImage() {}

    public AdditionalImage(String url, String caption, int order) {
        this.url = url;
        this.caption = caption;
        this.order = order;
    }

    // Parcelable implementation
    protected AdditionalImage(Parcel in) {
        url = in.readString();
        caption = in.readString();
        order = in.readInt();
    }

    public static final Creator<AdditionalImage> CREATOR = new Creator<AdditionalImage>() {
        @Override
        public AdditionalImage createFromParcel(Parcel in) {
            return new AdditionalImage(in);
        }

        @Override
        public AdditionalImage[] newArray(int size) {
            return new AdditionalImage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(caption);
        dest.writeInt(order);
    }

    // Getters and setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "AdditionalImage{" +
                "url='" + url + '\'' +
                ", caption='" + caption + '\'' +
                ", order=" + order +
                '}';
    }
}
