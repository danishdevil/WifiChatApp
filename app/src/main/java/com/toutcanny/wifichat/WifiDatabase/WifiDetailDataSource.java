package com.toutcanny.wifichat.WifiDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.toutcanny.wifichat.Data_Model.WifiDetail;

/**
 * Created by Farhan on 09-03-2016.
 */
public class WifiDetailDataSource {
    SQLiteOpenHelper sqLiteOpenHelper;
    SQLiteDatabase sqLiteDatabase;

    public WifiDetailDataSource(Context context)
    {
        this.sqLiteOpenHelper=new WifiDetails(context);
        open();
    }

    public void open()
    {
        sqLiteDatabase=sqLiteOpenHelper.getWritableDatabase();
    }

    public void close()
    {
        sqLiteOpenHelper.close();
    }

    public void storeWifiDetails(WifiDetail wifiDetail)
    {
        if(isWifiDetailPresent(wifiDetail))
            sqLiteDatabase.execSQL("DELETE FROM Wifi_Details where wifi_name='" + wifiDetail.getSSID() + "';");
        ContentValues values = new ContentValues();
        values.put("wifi_name", wifiDetail.getSSID());
        values.put("server_ip", wifiDetail.getIP_Address());
        values.put("server_port", wifiDetail.getPort());
        sqLiteDatabase.insert("Wifi_Details", null, values);
    }

    public WifiDetail getWifiDetails(String SSID) {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM Wifi_Details where wifi_name='" + SSID + "';", null);
        if (cursor.getCount() == 0)
            return null;
        else {
            cursor.moveToFirst();
            return new WifiDetail(cursor.getString(1), cursor.getString(2), cursor.getString(3));

        }
    }

    public boolean isWifiDetailPresent(WifiDetail wifiDetail)
    {
        Cursor cursor=sqLiteDatabase.rawQuery("SElECT * FROM Wifi_Details where wifi_name='" + wifiDetail.getSSID() + "'", null);
        if(cursor.getCount()==0)
            return false;
        else
            return true;
    }

}
