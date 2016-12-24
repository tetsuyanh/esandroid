package com.tetsuyanh.esandroid.db;

import com.tetsuyanh.esandroid.BuildConfig;
import com.tetsuyanh.esandroid.entity.Post;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tetsuyanh on 2016/12/17.
 */

public class BookmarkHelper {
    private static final String TAG = BookmarkHelper.class.getSimpleName();
    private static final String TABLE_NAME = "bookmarks";

    public static List<Post> getList(final Context context, final Integer teamId) {
        // must return array instance
        List<Post> list = new ArrayList<Post>();
        Cursor c = null;
        DataSQLiteHelper mHelper = null;
        try {
            mHelper = new DataSQLiteHelper(context);

            String sql = "select post_id, title from bookmarks where team_id = ? order by title";
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

    public static long insert(final Context context, final Integer teamId, final Post post) {
        ContentValues values = new ContentValues();
        values.put("post_id", post.GetId());
        values.put("team_id", teamId);
        values.put("title", post.GetTitle());
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