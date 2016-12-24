package com.tetsuyanh.esandroid.service;

import android.content.Context;
import android.util.Log;

import com.tetsuyanh.esandroid.db.HistoryHelper;
import com.tetsuyanh.esandroid.entity.Post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tetsuyanh on 2016/12/20.
 */

public class HistoryService {
    private static final String TAG = HistoryService.class.getSimpleName();
    private static final Integer HISTORY_SIZE = 20;

    private Context mContext;
    private Map<String, List<Post>> mTeamPostList;

    public HistoryService(Context context) {
        mContext = context;
        mTeamPostList = new HashMap<>();
    }

    public List<Post> GetList(String team) {
        return getList(team);
    }

    public boolean Push(String teamName, Post post) {
        List<Post> list = getList(teamName);
        // even if it fails on the way, remove first so as not to exceed the size
        if (list.contains(post)) {
            // remove same history if list already had
            if (!pop(teamName, post.getId())) {
                Log.e(TAG, "failed to pop previous same history");
                return false;
            }
        } else if (list.size() == HISTORY_SIZE) {
            // remove the oldest history if list size will be exceeded
            if (!pop(teamName, list.get(HISTORY_SIZE - 1).getId())) {
                Log.e(TAG, "failed to remove the oldest hitstory");
                return false;
            }
        }

        if (HistoryHelper.insert(mContext, teamName, post) != -1) {
            list.add(post);
            clearList(teamName);
            return true;
        } else {
            Log.e(TAG, "failed to insert history");
            return false;
        }
    }

    private boolean pop(String teamName, Integer postId) {
        List<Post> list = getList(teamName);
        int index = list.indexOf(new Post(postId, null));
        if (index == -1) {
            Log.e(TAG, "not found post");
            return false;
        }

        if (HistoryHelper.delete(mContext, teamName, postId) == 1) {
            list.remove(index);
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
            list = HistoryHelper.getList(mContext, teamName);
            mTeamPostList.put(teamName, list);
        }
        return list;
    }

    private void clearList(String teamName) {
        mTeamPostList.remove(teamName);
    }

}
