package com.toutcanny.wifichat.Helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Farhan on 08-03-2016.
 */
public class NamePreference {

    public static String getName(Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences("com.toutcanny.wifichat.username",context.MODE_PRIVATE);
        return sharedPref.getString("userName",null);
    }

    public static void setName(Context context,String userName)
    {
        SharedPreferences sharedPref = context.getSharedPreferences("com.toutcanny.wifichat.username",context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPref.edit();
        editor.putString("userName",userName);
        editor.commit();
    }

}
