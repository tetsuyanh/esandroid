package com.tetsuyanh.esandroid.entity;

/**
 * Created by tetsuyanh on 2016/12/18.
 */

public class Url {
    private final String teamName;
    private final String key;
    private final String url;

    public Url(String teamName, String key, String url) {
        this.teamName = teamName;
        this.key = key;
        this.url = url;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getUrl() {
        return url;
    }
}
