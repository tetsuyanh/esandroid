package com.tetsuyanh.esandroid.service;

import android.content.Context;
import android.util.Log;

import com.tetsuyanh.esandroid.db.TeamHelper;
import com.tetsuyanh.esandroid.entity.Post;
import com.tetsuyanh.esandroid.entity.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tetsuyanh on 2016/12/20.
 */

public class TeamManager {
    private static String TAG = TeamManager.class.getSimpleName();
    private static TeamManager mInstance;
    private static Map<String, Integer> mTeamIdSet;
    private static Context mContext;

    public static TeamManager GetInstance(Context context) {
        if (mInstance == null) {
            mContext = context;
            mInstance = new TeamManager();
            mTeamIdSet = new HashMap<>();
        }
        return mInstance;
    }

    public synchronized Integer getTeamId(String teamName) {
        Log.d(TAG, "*** getTeamId from mem : " + teamName);
        Integer teamId = mTeamIdSet.get(teamName);
        if (teamId == null) {
            Log.d(TAG, "**** getTeamId from db");
            Team team = TeamHelper.getTeam(mContext, teamName);
            if (team != null) {
                Log.d(TAG, "***** team found");
                teamId = team.GetId();
            } else {
                Log.d(TAG, "***** team not found");
                teamId = (int) TeamHelper.insert(mContext, teamName);
                if (teamId == -1) {
                    Log.e(TAG, "failed to insert team");
                    return null;
                }
            }
            Log.d(TAG, "**** getTeamId fixed : " + teamId);
            mTeamIdSet.put(teamName, teamId);
        }
        return teamId;
    }

}
