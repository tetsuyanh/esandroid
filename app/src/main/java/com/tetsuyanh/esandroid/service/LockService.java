package com.tetsuyanh.esandroid.service;

import android.content.Context;
import android.util.Log;

import com.tetsuyanh.esandroid.db.LockHelper;
import com.tetsuyanh.esandroid.entity.Post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tetsuyanh on 2016/01/01.
 */

public class LockService {
    private static final String TAG = LockService.class.getSimpleName();
    private static final Integer LOCK_SIZE = 20;
    private static final Integer EXPIRE_MSEC = 30 * 1000; // 10 min

    private Context mContext;
    private Map<String, List<Post>> mTeamPostList;

    public LockService(Context context) {
        mContext = context;
        mTeamPostList = new HashMap<>();
    }

    public boolean isExpire(String teamName, Integer postId) {
        Log.d(TAG, "isExpire");
        List<Post> list = list(teamName);
        int idx = list.indexOf(new Post(postId, null));
        Log.d(TAG, "idx: " + idx);
        if (idx == -1) {
            return false;
        }
        Log.d(TAG, "val1: " + list.get(idx).getUpdatedAt());
        Log.d(TAG, "val2: " + EXPIRE_MSEC);
        Log.d(TAG, "val3: " + System.currentTimeMillis());

        return (list.get(idx).getUpdatedAt() + EXPIRE_MSEC) < System.currentTimeMillis();
    }

    public boolean has(String teamName, Integer postId) {
        return list(teamName).contains(new Post(postId, null));
    }

    public List<Post> getList(String teamName) {
        return list(teamName);
    }

    public boolean push(String teamName, Post post) {
        List<Post> list = list(teamName);
        if (list.size() >= LOCK_SIZE) {
            Log.e(TAG, "lock full size");
            return false;
        }

        if (LockHelper.insert(mContext, teamName, post) != -1) {
            list.add(post);
            clearList(teamName);
            return true;
        } else {
            Log.e(TAG, "failed to insert post");
            return false;
        }
    }

    public boolean pop(String teamName, Integer postId) {
        List<Post> list = list(teamName);
        if (LockHelper.delete(mContext, teamName, postId) == 1) {
            list.remove(new Post(postId, null));
            clearList(teamName);
            return true;
        } else {
            Log.e(TAG, "failed to delete post");
            return false;
        }
    }

    public boolean update(String teamName, Integer postId) {
        List<Post> list = list(teamName);
        if (LockHelper.update(mContext, teamName, postId) == -1) {
            clearList(teamName);
            return true;
        } else {
            Log.e(TAG, "failed to update post");
            return false;
        }
    }

    private List<Post> list(String teamName) {
        List<Post> list = mTeamPostList.get(teamName);
        if (list == null) {
            list = LockHelper.getList(mContext, teamName);
            mTeamPostList.put(teamName, list);
        }
        return list;
    }

    private void clearList(String teamName) {
        mTeamPostList.remove(teamName);
    }
}
