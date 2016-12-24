package com.tetsuyanh.esandroid.db;

import com.tetsuyanh.esandroid.BuildConfig;
import com.tetsuyanh.esandroid.entity.Url;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * Created by tetsuyanh on 2016/12/17.
 */

public class UrlHelper {
    private static final String TAG = UrlHelper.class.getSimpleName();
    private static final String TABLE_NAME = "urls";

    public static Url get(final Context context, final String teamName, final String key) {
        Url url = null;
        Cursor c = null;
        DataSQLiteHelper mHelper = null;
        try {
            mHelper = new DataSQLiteHelper(context);
            String sql;
            String[] args;
            if (teamName == null) {
                sql = "select team_name, key, url from urls where key = ?";
                args = new String[]{key};
            } else {
                sql = "select team_name, key, url from urls where team_name = ? and key = ?";
                args = new String[]{teamName, key};
            }
            if (BuildConfig.IS_DEBUG) {
                Log.d(TAG, "sql:" + sql);
            }
            c = mHelper.mDb.rawQuery(sql, args);
            boolean isResult = c.moveToFirst();
            if (isResult) {
                url = new Url(c.getString(0), c.getString(1), c.getString(2));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (c != null) {
                c.close();
            }
            if (mHelper != null) {
                mHelper.cleanup();
            }
        }
        return url;
    }

    public static long insert(final Context context, final String teamName, final String key, final String url) {
        ContentValues values = new ContentValues();
        values.put("team_name", teamName);
        values.put("key", key);
        values.put("url", url);
        values.put("created_at", (int)System.currentTimeMillis());
        DataSQLiteHelper mHelper = new DataSQLiteHelper(context);
        long result = mHelper.mDb.insert(TABLE_NAME, null, values);
        mHelper.cleanup();
        return result;
    }

    public static long update(final Context context, final String teamName, final String key, final String url) {
        ContentValues values = new ContentValues();
        values.put("url", url);
        values.put("updated_at", (int)System.currentTimeMillis());
        DataSQLiteHelper mHelper = new DataSQLiteHelper(context);
        long result = mHelper.mDb.update(TABLE_NAME, values, "team_name = ? and key = ?", new String[]{teamName, key});
        mHelper.cleanup();
        return result;
    }
}