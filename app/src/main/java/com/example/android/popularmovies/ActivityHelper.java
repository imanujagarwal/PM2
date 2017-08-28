package com.example.android.popularmovies;

import android.content.Context;

/**
 * Created by anuj on 28/8/17.
 */

public class ActivityHelper {

    private static Context localContext;

    public static Context getLocalContext(){
        return localContext;
    }

    public static void setLocalContext(Context context){
        localContext = context;
    }
}
