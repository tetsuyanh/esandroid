package com.tetsuyanh.esandroid.esa;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tetsuyanh on 2016/12/06.
 */

public class EsaWeb {

    public static final String URL_TOP = "https://esa.io";

    public static final String REGEXP_HOST = "^https:\\/\\/([0-9a-z-]+).esa.io";
    public static final String REGEXP_POST = "^https:\\/\\/[0-9a-z-]+.esa.io/posts/(\\d+)(#([0-9-])+)?$";
    public static final String REGEXP_TITLE = "^(.+) - [0-9a-z-]+.esa.io$";

    public static final String FORMAT_POST = "https://%s.esa.io/posts/%s";

    public static String GetTeam(String url) {
        Pattern p = Pattern.compile(REGEXP_HOST);
        Matcher m = p.matcher(url);
        return (m != null && m.find()) ? m.group(1) : null;
    }

    public static String GetTitle(String pageTitle) {
        Pattern p = Pattern.compile(REGEXP_TITLE);
        Matcher m = p.matcher(pageTitle);
        return (m != null && m.find()) ? m.group(1) : null;
    }

    public static Integer GetPostId(String url) {
        Pattern p = Pattern.compile(REGEXP_POST);
        Matcher m = p.matcher(url);
        if (m == null || !m.find()) {
            return null;
        }

        try {
            return Integer.parseInt(m.group(1));
        } catch (Exception e) {
            return null;
        }
    }

    public static String GetPost(String team, String postId) {
        return String.format(FORMAT_POST, team, postId);
    }

}
