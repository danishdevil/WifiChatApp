package com.toutcanny.wifichat.Helper;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

/**
 * Created by Farhan on 23-03-2016.
 */
public class IpGetter {
    public static String getIP(Context context)
    {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }
}
