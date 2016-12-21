package com.tetsuyanh.esandroid.db;

import com.tetsuyanh.esandroid.BuildConfig;
import com.tetsuyanh.esandroid.entity.Team;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tetsuyanh on 2016/12/17.
 */

public class TeamHelper {
    private static final String TAG = TeamHelper.class.getSimpleName();
    private static final String TABLE_NAME = "teams";

    public static List<Team> getTeamList(final Context context) {
        List<Team> list = new ArrayList<Team>();
        Team Team = null;
        Cursor c = null;
        DataSQLiteHelper DataHelper = null;
        try {
            DataHelper = new DataSQLiteHelper(context);

            String sql = "select _id, name from teams";
            if (BuildConfig.IS_DEBUG) {
                Log.d(TAG, "sql:" + sql);
            }
            c = DataHelper.mDb.rawQuery(sql, null);
            boolean isResult = c.moveToFirst();
            while (isResult) {
                Team team = new Team(c.getInt(0), c.getString(1));
                list.add(team);
                isResult = c.moveToNext();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (c != null) {
                c.close();
            }
            if (DataHelper != null) {
                DataHelper.cleanup();
            }
        }
        return list;
    }

    public static Team getTeam(final Context context, final String name) {
        Team team = null;
        Cursor c = null;
        DataSQLiteHelper DataHelper = null;
        try {
            DataHelper = new DataSQLiteHelper(context);
            String sql = "select _id, name from teams where name = ?";
            if (BuildConfig.IS_DEBUG) {
                Log.d(TAG, "sql:" + sql);
            }
            c = DataHelper.mDb.rawQuery(sql, new String[]{name});
            boolean isResult = c.moveToFirst();
            if (isResult) {
                team = new Team(c.getInt(0), c.getString(1));
                isResult = c.moveToNext();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (c != null) {
                c.close();
            }
            if (DataHelper != null) {
                DataHelper.cleanup();
            }
        }
        return team;
    }

    public static long insert(final Context context, final String name) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        DataSQLiteHelper DataHelper = new DataSQLiteHelper(context);
        long result = DataHelper.mDb.insert(TABLE_NAME, null, values);
        DataHelper.cleanup();
        return result;
    }
}