package com.tetsuyanh.esandroid.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tetsuyanh on 2016/12/18.
 */

public class Url implements Parcelable {
    private String key;
    private String url;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(key);
        out.writeString(url);
    }

    public static final Creator<Url> CREATOR
            = new Creator<Url>() {
        public Url createFromParcel(Parcel in) {
            return new Url(in);
        }

        public Url[] newArray(int size) {
            return new Url[size];
        }
    };

    public Url(String key, String url) {
        this.key = key;
        this.url = url;
    }

    private Url(Parcel in) {
        key = in.readString();
        url = in.readString();
    }

    public String GetUrl() {
        return url;
    }
}
