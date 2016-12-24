package com.tetsuyanh.esandroid.service;

import android.content.Context;
import android.util.Log;

import com.tetsuyanh.esandroid.db.BookmarkHelper;
import com.tetsuyanh.esandroid.entity.Post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tetsuyanh on 2016/12/20.
 */

public class BookmarkService {
    private static final String TAG = BookmarkService.class.getSimpleName();
    private static final Integer BOOKMARK_SIZE = 3;

    private Context mContext;
    private Map<String, List<Post>> mTeamPostList;

    public BookmarkService(Context context) {
        mContext = context;
        mTeamPostList = new HashMap<>();
    }

    public boolean Has(String teamName, Integer postId) {
        return getList(teamName).contains(new Post(postId, null));
    }

    public List<Post> GetList(String team) {
        return getList(team);
    }

    public boolean Push(String teamName, Post post) {
        List<Post> list = getList(teamName);
        if (list.size() >= BOOKMARK_SIZE) {
            Log.e(TAG, "bookmark full size");
            return false;
        }

        if (BookmarkHelper.insert(mContext, teamName, post) != -1) {
            list.add(post);
            clearList(teamName);
            return true;
        } else {
            Log.e(TAG, "failed to insert post");
            return false;
        }
    }

    public boolean Pop(String teamName, Integer postId) {
        List<Post> list = getList(teamName);
        if (BookmarkHelper.delete(mContext, teamName, postId) == 1) {
            list.remove(new Post(postId, null));
            clearList(teamName);
            return true;
        } else {
            Log.e(TAG, "failed to delete post");
            return false;
        }
    }

    private List<Post> getList(String teamName) {
        List<Post> list = mTeamPostList.get(teamName);
        if (list == null) {
            list = BookmarkHelper.getList(mContext, teamName);
            mTeamPostList.put(teamName, list);
        }
        return list;
    }

    private void clearList(String teamName) {
        mTeamPostList.remove(teamName);
    }
}
