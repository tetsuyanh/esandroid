package com.tetsuyanh.esandroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by tetsuyanh on 2016/12/17.
 */

public class DataSQLiteHelper {
    public static final String TAG = DataSQLiteHelper.class.getSimpleName();

    public SQLiteDatabase mDb;
    private final DataSQLiteOpenHelper mOpenHelper;

    public DataSQLiteHelper(final Context context) {
        mOpenHelper = new DataSQLiteOpenHelper(context);
        createDataBase();
    }

    private void createDataBase() {
        if (mDb == null) {
            mDb = mOpenHelper.getWritableDatabase();
        }
    }

    public void cleanup() {
        if (mDb != null) {
            mDb.close();
            mDb = null;
        }
    }
}