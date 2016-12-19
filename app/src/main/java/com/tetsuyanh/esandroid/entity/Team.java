package com.tetsuyanh.esandroid.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tetsuyanh on 2016/12/18.
 */

public class Team implements Parcelable {

    private static final String TABLE_NAME = "teams";

    private Integer id;
    private String name;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
    }

    public static final Creator<Team> CREATOR
            = new Creator<Team>() {
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }

        public Team[] newArray(int size) {
            return new Team[size];
        }
    };

    public Team(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    private Team(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
    }

    public Integer GetId() {
        return id;
    }

    public String GetName() {
        return name;
    }
}
