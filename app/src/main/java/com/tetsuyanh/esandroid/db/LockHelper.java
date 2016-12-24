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
 * Created by tetsuyanh on 2017/01/01.
 */

public class LockHelper {
    private static final String TAG = LockHelper.class.getSimpleName();
    private static final String TABLE_NAME = "locks";

    public static List<Post> getList(final Context context, final String teamName) {
        // must return array instance
        List<Post> list = new ArrayList<>();
        Cursor c = null;
        DataSQLiteHelper mHelper = null;
        try {
            mHelper = new DataSQLiteHelper(context);

            String sql = "select post_id, title, updated_at from locks where team_name = ? order by title";
            if (BuildConfig.IS_DEBUG) {
                Log.d(TAG, "sql:" + sql);
            }
            c = mHelper.mDb.rawQuery(sql, new String[]{teamName});
            boolean isResult = c.moveToFirst();
            while (isResult) {
                Post post = new Post(c.getInt(0), c.getString(1));
                post.setUpdatedAt(c.getInt(2));
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

    public static long insert(final Context context, final String teamName, final Post post) {
        ContentValues values = new ContentValues();
        values.put("post_id", post.getId());
        values.put("team_name", teamName);
        values.put("title", post.getTitle());
        values.put("created_at", (int)System.currentTimeMillis());
        values.put("updated_at", (int)System.currentTimeMillis());
        DataSQLiteHelper mHelper = new DataSQLiteHelper(context);
        long result = mHelper.mDb.insert(TABLE_NAME, null, values);
        mHelper.cleanup();
        return result;
    }

    public static long update(final Context context, final String teamName, final Integer postId) {
        ContentValues values = new ContentValues();
        values.put("updated_at", (int)System.currentTimeMillis());
        DataSQLiteHelper mHelper = new DataSQLiteHelper(context);
        long result = mHelper.mDb.update(TABLE_NAME, values, "team_name = ? and post_id = ?", new String[]{teamName, postId.toString()});
        mHelper.cleanup();
        return result;
    }

    public static long delete(final Context context, final String teamName, final Integer postId) {
        DataSQLiteHelper mHelper = new DataSQLiteHelper(context);
        int result = mHelper.mDb.delete(TABLE_NAME, "team_name = ? and post_id = ?", new String[]{teamName, postId.toString()});
        mHelper.cleanup();
        return result;
    }
}