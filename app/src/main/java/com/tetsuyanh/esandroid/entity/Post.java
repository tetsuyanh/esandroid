package com.tetsuyanh.esandroid.entity;

/**
 * Created by tetsuyanh on 2016/12/14.
 */

public class Post {
    private final Integer id;
    private final String title;

    public Post(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Post)) {
            return false;
        }
        Post other = (Post)obj;
        return this.id.compareTo(other.id) == 0;
    }
}
