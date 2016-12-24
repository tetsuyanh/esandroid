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

    public String GetLatestUrl() {
        Url url = UrlHelper.get(mContext, URL_KEY_LATEST);
        return url != null ? url.GetUrl() : null;
    }

    public boolean SetLatestUrl(String url) {
        String current = GetLatestUrl();
        if (current == null) {
            return UrlHelper.insert(mContext, URL_KEY_LATEST, url) != -1;
        } else if (!current.equals(url)) {
            return UrlHelper.update(mContext, URL_KEY_LATEST, url) == 1;
        } else {
            return true;
        }
    }

}
