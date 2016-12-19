package com.tetsuyanh.esandroid.db;

import com.tetsuyanh.esandroid.entity.Post;
import com.tetsuyanh.esandroid.entity.Team;
import com.tetsuyanh.esandroid.entity.Url;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tetsuyanh on 2016/12/14.
 */

public class DataSQLite implements Data {

    private static final String TAG = "UrlHelper";
    private static final String URL_KEY_LATEST = "latest";

    private Context mContext;
    private Map<String, Integer> mTeamIdSet;

    public DataSQLite(Context context) {
        mContext = context;

        mTeamIdSet = new HashMap<String, Integer>();
        List<Team> teamList = TeamHelper.getTeamList(mContext);
        for(Team team: teamList) {
            mTeamIdSet.put(team.GetName(), team.GetId());
        }
    }

    @Override
    public String GetLatestUrl() {
        Url url = UrlHelper.getUrl(mContext, URL_KEY_LATEST);
        return url != null ? url.GetUrl() : null;
    }

    @Override
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

    @Override
    public boolean HasBookmark(String team, Integer postId) {
        return BookmarkHelper.getBookmark(mContext, getTeamId(team), postId) != null;
    }

    @Override
    public List<Post> GetBookmarks(String team) {
        return BookmarkHelper.getBookmarkList(mContext, getTeamId(team));
    }

    @Override
    public boolean PushBookmark(String team, Post post) {
        return BookmarkHelper.insert(mContext, getTeamId(team), post) != -1;
    }

    @Override
    public boolean PopBookmark(String team, Integer postId) {
        return BookmarkHelper.delete(mContext, getTeamId(team), postId) == 1;
    }

    /*@Override
    public List<Post> GetHistories(Integer teamId) {
        return loadList(createListKey(Format.HISTORY, team));
    }

    @Override
    public boolean PushHistory(Integer teamId, Post post) {
        ListKey key = createListKey(Format.HISTORY, team);
        List<Post> list = loadList(key);
        if (list.contains(key)) {
            list.remove(key);
        }
        list.add(0, post);
        if (list.size() > HISTORY_SIZE) {
            list.remove(HISTORY_SIZE);
        }
        return saveList(key, list);
    }*/

    private Integer getTeamId(String name) {
        Integer teamId = mTeamIdSet.get(name);
        if (teamId == null) {
            long id = TeamHelper.insert(mContext, name);
            if (id != -1 ) {
                teamId = (int)id;
                mTeamIdSet.put(name, teamId);
            }
        }

        return teamId;
    }
}
