package com.tetsuyanh.esandroid;

import android.content.Context;

/**
 * Created by tetsuyanh on 2017/01/01.
 */

public class ActionManager {

    private static ActionManager mInstance;


    public static ActionManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ActionManager();
        }
        return mInstance;
    }

    public boolean lock() throws Exception {
        return false;
    }

    public boolean unlock() {
        return false;
    }
}
