package com.tetsuyanh.esandroid.entity;

/**
 * Created by tetsuyanh on 2016/12/14.
 */

public class Post {

    public static final String TAG = "Post";

    private Integer id;
    private String title;

    public Post(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public Integer GetId() {
        return id;
    }

    public String GetTitle() {
        return title;
    }

}
