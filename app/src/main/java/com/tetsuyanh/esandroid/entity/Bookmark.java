package com.tetsuyanh.esandroid.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tetsuyanh on 2016/12/18.
 */

public class Bookmark implements Parcelable {

    private Integer teamId;
    private Integer postId;
    private String title;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(teamId);
        out.writeInt(postId);
        out.writeString(title);
    }

    public static final Parcelable.Creator<Bookmark> CREATOR
            = new Parcelable.Creator<Bookmark>() {
        public Bookmark createFromParcel(Parcel in) {
            return new Bookmark(in);
        }

        public Bookmark[] newArray(int size) {
            return new Bookmark[size];
        }
    };

    public Bookmark(Integer teamId, Integer postId, String title) {
        this.teamId = teamId;
        this.postId = postId;
        this.title = title;
    }

    private Bookmark(Parcel in) {
        teamId = in.readInt();
        postId = in.readInt();
        title = in.readString();
    }

}
