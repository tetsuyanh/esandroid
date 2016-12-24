package com.tetsuyanh.esandroid.service;

import android.content.Context;
import android.util.Log;

import com.tetsuyanh.esandroid.db.HistoryHelper;
import com.tetsuyanh.esandroid.entity.Post;
import com.tetsuyanh.esandroid.entity.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tetsuyanh on 2016/12/20.
 */

public class HistoryService {
    private static final String TAG = HistoryService.class.getSimpleName();
    private static final Integer HISTORY_SIZE = 3;

    private Context mContext;
    private TeamManager mTeamManager;
    private Map<String, List<Post>> mTeamPostList;

    public HistoryService(Context context) {
        mContext = context;
        mTeamManager = TeamManager.GetInstance(context);
        mTeamPostList = new HashMap<>();
    }

    public List<Post> GetList(String team) {
        return getList(team);
    }

    public boolean Push(String team, Post post) {
        List<Post> list = getList(team);
        // even if it fails on the way, remove first so as not to exceed the size
        if (list.contains(post)) {
            // remove same history if list already had
            if (Pop(team, post.GetId()) == false) {
                Log.e(TAG, "failed to pop previous same history");
                return false;
            }
        } else if (list.size() == HISTORY_SIZE) {
            // remove the oldest history if list size will be exceeded
            if (Pop(team, list.get(HISTORY_SIZE - 1).GetId()) == false) {
                Log.e(TAG, "failed to remove the oldest hitstory");
                return false;
            }
        }

        if (HistoryHelper.insert(mContext, mTeamManager.getTeamId(team), post) != -1) {
            list.add(post);
            //mIsUpdated = true;
            clearList(team);
            return true;
        } else {
            Log.e(TAG, "failed to insert history");
            return false;
        }
    }

    public boolean Pop(String team, Integer postId) {
        List<Post> list = getList(team);
        int index = list.indexOf(new Post(postId, null));
        if (index == -1) {
            Log.e(TAG, "not found post");
            return false;
        }

        if (HistoryHelper.delete(mContext, mTeamManager.getTeamId(team), postId) == 1) {
            list.remove(index);
            //mIsUpdated = true;
            clearList(team);
            return true;
        } else {
            Log.e(TAG, "failed to delete post");
            return false;
        }
    }

    private List<Post> getList(String teamName) {
        List<Post> list = mTeamPostList.get(teamName);
        if (list == null) {
            list = HistoryHelper.getHistoryList(mContext, mTeamManager.getTeamId(teamName));
            mTeamPostList.put(teamName, list);
        }
        return list;
    }

    private void clearList(String teamName) {
        mTeamPostList.remove(teamName);
    }

}
