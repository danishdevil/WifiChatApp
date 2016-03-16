package com.toutcanny.wifichat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.toutcanny.wifichat.Helper.NetworkChange;


//Broadcast Reciever to Notify Activity that wifi has been disconnected
public class Network_Change_Reciever extends BroadcastReceiver {

    private static NetworkChange networkChange;

    public static void  setNetworkChange(NetworkChange networkChanger)
    {
        networkChange=networkChanger;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        networkChange.WifiStateChanged();
    }

}
