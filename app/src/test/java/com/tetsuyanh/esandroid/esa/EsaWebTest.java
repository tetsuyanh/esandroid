package com.tetsuyanh.esandroid.esa;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by tetsuyanh on 2016/12/23.
 */
public class EsaWebTest {
    private final String URL_ESA_ROOT = "https://esa.io/concept";
    private final String URL_OTHER = "https://google.com";

    private final String URL_MYTEAM_POST_10 = "https://myteam.esa.io/posts/10";
    private final String URL_MYTEAM_POST_10_ANCHOR = "https://myteam.esa.io/posts/10#2-1-0";
    private final String URL_MYTEAM_POST_ILLEGAL = "https://myteam.esa.io/posts/abc";
    private final String URL_MYTEAMSUB_POST_10 = "https://myteam-sub.esa.io/posts/10";

    @Test
    public void isInternal() throws Exception {
        assertEquals(true, EsaWeb.isInternal(URL_ESA_ROOT));
        assertEquals(true, EsaWeb.isInternal(URL_MYTEAM_POST_10));
        assertEquals(true, EsaWeb.isInternal(URL_MYTEAM_POST_10_ANCHOR));
        assertEquals(true, EsaWeb.isInternal(URL_MYTEAMSUB_POST_10));
        assertEquals(false, EsaWeb.isInternal(URL_OTHER));
    }

    @Test
    public void getTeam() throws Exception {
        assertEquals(null, EsaWeb.getTeam(URL_ESA_ROOT));
        assertEquals("myteam", EsaWeb.getTeam(URL_MYTEAM_POST_10));
        assertEquals("myteam", EsaWeb.getTeam(URL_MYTEAM_POST_ILLEGAL));
        assertEquals("myteam-sub", EsaWeb.getTeam(URL_MYTEAMSUB_POST_10));
        assertEquals(null, EsaWeb.getTeam(URL_OTHER));
    }

    @Test
    public void getPostId() throws Exception {
        assertEquals(Integer.valueOf(10), EsaWeb.getPostId(URL_MYTEAM_POST_10));
        assertEquals(Integer.valueOf(10), EsaWeb.getPostId(URL_MYTEAM_POST_10_ANCHOR));
        assertEquals(null, EsaWeb.getPostId(URL_MYTEAM_POST_ILLEGAL));
        assertEquals(null, EsaWeb.getPostId(URL_OTHER));
    }

    @Test
    public void getPostUrl() throws Exception {
        assertEquals(URL_MYTEAM_POST_10, EsaWeb.getPostUrl("myteam", 10));
        assertEquals(URL_MYTEAMSUB_POST_10, EsaWeb.getPostUrl("myteam-sub", 10));
    }

    @Test
    public void getPostTitle() throws Exception {
        assertEquals("title", EsaWeb.getPostTitle("title - myteam.esa.io"));
        assertEquals("title", EsaWeb.getPostTitle("[WIP] title - myteam.esa.io"));
        assertEquals("dir/title", EsaWeb.getPostTitle("dir/title - myteam.esa.io"));
    }

}
