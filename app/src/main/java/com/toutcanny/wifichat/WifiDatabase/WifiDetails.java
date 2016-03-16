package com.toutcanny.wifichat.WifiDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Farhan on 09-03-2016.
 */
public class WifiDetails extends SQLiteOpenHelper {

    final static String DB_NAME="wifi_detail.db";
    public static final int DB_VERSION = 1;

    public WifiDetails(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Wifi_Details(" +
                "wifi_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "wifi_name varchar(32)," +
                "server_ip varchar(30)," +
                "server_port varchar(10));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
