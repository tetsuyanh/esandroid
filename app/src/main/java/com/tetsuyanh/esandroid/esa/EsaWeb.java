package com.tetsuyanh.esandroid.esa;

import android.util.Log;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tetsuyanh on 2016/12/06.
 */

public class EsaWeb {
    public static final String URL_ROOT = "https://esa.io";

    private static final Pattern sPatternHostALl = Pattern.compile("^([0-9a-z-.]*)esa.io$");
    private static final Pattern sPatternHostTeam = Pattern.compile("^([0-9a-z-]+).esa.io$");
    private static final Pattern sPatternPathPost = Pattern.compile("^/posts/([0-9]+)(#([0-9-])+)?$");
    private static final String sPatternTitle = "^(\\[WIP\\] )?(.+) - [0-9a-z-]+.esa.io$";
    private static final String sFormatPost = "https://%s.esa.io/posts/%d";
    private static final Set<String> sDomainSetGoogleAuth = new HashSet<>(Arrays.asList(
            "accounts.google.com",
            "accounts.google.co.jp",
            "accounts.youtube.com"
    ));

    public static boolean isInternal(String urlString) {
        try {
            String host = new URL(urlString).getHost();
            Matcher m = sPatternHostALl.matcher(host);
            return m.matches() || sDomainSetGoogleAuth.contains(host);
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

    public static boolean isPathRoot(String urlString) {
        try {
            String path = new URL(urlString).getPath();
            return path.length() == 0 || path.equals("/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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

    public static String getPostTitle(String pageTitle) {
        Pattern p = Pattern.compile(sPatternTitle);
        Matcher m = p.matcher(pageTitle);
        return m.find() ? m.group(2) : null;
    }

    public static String getPostUrl(String teamName, int postId) {
        return String.format(Locale.JAPAN, sFormatPost, teamName, postId);
    }

}
