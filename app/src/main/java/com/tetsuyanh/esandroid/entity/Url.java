package com.tetsuyanh.esandroid.entity;

/**
 * Created by tetsuyanh on 2016/12/18.
 */

public class Url {
    private final String key;
    private final String url;

    public Url(String key, String url) {
        this.key = key;
        this.url = url;
    }

    public String GetUrl() {
        return url;
    }
}
