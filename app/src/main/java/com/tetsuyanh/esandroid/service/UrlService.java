package com.tetsuyanh.esandroid.service;

import android.content.Context;

import com.tetsuyanh.esandroid.db.UrlHelper;
import com.tetsuyanh.esandroid.entity.Url;

/**
 * Created by tetsuyanh on 2016/12/20.
 */

public class UrlService {
    private static final String URL_KEY_LATEST = "latest";
    private Context mContext;

    public UrlService(Context context) {
        mContext = context;
    }

    public Url getLatestUrl(String teamName) {
        return UrlHelper.get(mContext, teamName, URL_KEY_LATEST);
    }

    public boolean setLatestUrl(String teamName, String url) {
        Url u = getLatestUrl(teamName);
        if (u == null) {
            return UrlHelper.insert(mContext, teamName, URL_KEY_LATEST, url) != -1;
        } else if (!u.getUrl().equals(url)) {
            return UrlHelper.update(mContext, teamName, URL_KEY_LATEST, url) == 1;
        } else {
            return true;
        }
    }

}
