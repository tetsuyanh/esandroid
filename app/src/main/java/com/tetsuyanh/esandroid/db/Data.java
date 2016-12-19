package com.tetsuyanh.esandroid.db;

import com.tetsuyanh.esandroid.entity.Post;

import java.util.List;

/**
 * Created by tetsuyanh on 2016/12/14.
 */

public interface Data {

    final int HISTORY_SIZE = 20;

    public String GetLatestUrl();
    public boolean SetLatestUrl(String url);

    public List<Post> GetBookmarks(String team);
    public boolean HasBookmark(String team, Integer postId);
    public boolean PushBookmark(String team, Post post);
    public boolean PopBookmark(String team, Integer postId);

    //public List<Post> GetHistories(String team);
    //public boolean PushHistory(String team, Post post);
    //public boolean PopHistory(String team, String postId);

}
