package com.tetsuyanh.esandroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.tetsuyanh.esandroid.BuildConfig;
import com.tetsuyanh.esandroid.entity.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tetsuyanh on 2016/12/17.
 */

public class HistoryHelper {
    private static final String TAG = HistoryHelper.class.getSimpleName();
    private static final String TABLE_NAME = "histories";

    public static List<Post> getHistoryList(final Context context, final Integer teamId) {
        // must return array instance
        List<Post> list = new ArrayList<Post>();
        Post Post = null;
        Cursor c = null;
        DataSQLiteHelper mHelper = null;
        try {
            mHelper = new DataSQLiteHelper(context);

            String sql = "select post_id, title from histories where team_id = ? order by created_at desc";
            if (BuildConfig.IS_DEBUG) {
                Log.d(TAG, "sql:" + sql);
            }
            c = mHelper.mDb.rawQuery(sql, new String[]{teamId.toString()});
            boolean isResult = c.moveToFirst();
            while (isResult) {
                Post post = new Post(c.getInt(0), c.getString(1));
                list.add(post);
                isResult = c.moveToNext();
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
        return list;
    }

    public static Post getHistory(final Context context, final Integer teamId, final Integer postId) {
        Post post = null;
        Cursor c = null;
        DataSQLiteHelper mHelper = null;
        try {
            mHelper = new DataSQLiteHelper(context);
            String sql = "select post_id, title from histories where team_id = ? and post_id = ?";
            if (BuildConfig.IS_DEBUG) {
                Log.d(TAG, "sql:" + sql);
            }
            c = mHelper.mDb.rawQuery(sql, new String[]{teamId.toString(), postId.toString()});
            boolean isResult = c.moveToFirst();
            if (isResult) {
                post = new Post(c.getInt(0), c.getString(1));
                isResult = c.moveToNext();
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
        return post;
    }

    public static long insert(final Context context, final Integer teamId, final Post post) {
        ContentValues values = new ContentValues();
        values.put("post_id", post.GetId());
        values.put("team_id", teamId);
        values.put("title", post.GetTitle());
        values.put("created_at", (int)System.currentTimeMillis());
        DataSQLiteHelper mHelper = new DataSQLiteHelper(context);
        long result = mHelper.mDb.insert(TABLE_NAME, null, values);
        mHelper.cleanup();
        return result;
    }

    public static long delete(final Context context, final Integer teamId, final Integer postId) {
        DataSQLiteHelper mHelper = new DataSQLiteHelper(context);
        int result = mHelper.mDb.delete(TABLE_NAME, "team_id = ? and post_id = ?", new String[]{teamId.toString(), postId.toString()});
        mHelper.cleanup();
        return result;
    }
}