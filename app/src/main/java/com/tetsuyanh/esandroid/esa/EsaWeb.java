package com.tetsuyanh.esandroid.esa;

import android.util.Log;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tetsuyanh on 2016/12/06.
 */

public class EsaWeb {
    private static final String TAG = EsaWeb.class.getSimpleName();

    public static final String DOMAIN = "esa.io";
    public static final String URL_ROOT = "https://" + DOMAIN;

    private static Pattern sPatternHostALl= Pattern.compile("^([0-9a-z-.]*)" + DOMAIN + "$");
    private static Pattern sPatternHostTeam= Pattern.compile("^([0-9a-z-]+)." + DOMAIN + "$");
    private static Pattern sPatternPathPost = Pattern.compile("^/posts/([0-9]+)(#([0-9-])+)?$");
    private static final String sPatternPostTitle = "^(.+) - [0-9a-z-]+." + DOMAIN + "$";
    private static final String sFormatPost = "https://%s." + DOMAIN + "/posts/%d";

    public static boolean isHost(String urlString) {
        try {
            Matcher m = sPatternHostALl.matcher(new URL(urlString).getHost());
            return m.matches();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getTeam(String urlString) {
        try {
            Matcher m = sPatternHostTeam.matcher(new URL(urlString).getHost());
            return m.find() ? m.group(1) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer getPostId(String urlString) {
        try {
            Matcher m = sPatternPathPost.matcher(new URL(urlString).getPath());
            return m.find() ? Integer.parseInt(m.group(1)) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPostUrl(String teamName, int postId) {
        return String.format(sFormatPost, teamName, postId);
    }

    public static String getPostTitle(String pageTitle) {
        Pattern p = Pattern.compile(sPatternPostTitle);
        Matcher m = p.matcher(pageTitle);
        return (m != null && m.find()) ? m.group(1) : null;
    }

}
