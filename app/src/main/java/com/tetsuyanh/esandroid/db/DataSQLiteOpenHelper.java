package com.tetsuyanh.esandroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by tetsuyanh on 2016/12/17.
 */

public class DataSQLiteOpenHelper extends SQLiteOpenHelper {
    public Context mContext;
    public static final String TAG = "DataSQLiteOpenHelper";
    public static final String DB_NAME = "esandroid";
    public static final int DB_VERSION = 1;

    public DataSQLiteOpenHelper(final Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        Log.d(TAG, "onCreate version : " + db.getVersion());
        execFileSQL(db, "create_table.sql");
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        Log.d(TAG, "onUpgrade version : " + db.getVersion());
        Log.d(TAG, "onUpgrade oldVersion : " + oldVersion);
        Log.d(TAG, "onUpgrade newVersion : " + newVersion);
    }

   private void execFileSQL(SQLiteDatabase db, String fileName){
        InputStream in = null;
        InputStreamReader inReader = null;
        BufferedReader reader = null;
        try {
            in = mContext.getAssets().open(fileName);
            inReader = new InputStreamReader(in, "UTF-8");
            reader = new BufferedReader(inReader);

            String s;
            while((s = reader.readLine()) != null){
                s = s.trim();
             if (0 < s.length()){
                    db.execSQL(s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inReader != null) {
                try {
                    inReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}