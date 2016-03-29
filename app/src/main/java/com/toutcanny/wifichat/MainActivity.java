package com.toutcanny.wifichat;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.toutcanny.wifichat.Helper.NamePreference;
import com.toutcanny.wifichat.Helper.NetworkChange;

public class MainActivity extends AppCompatActivity implements NetworkChange {

    TextView isWifiConnected;
    Button next;
    Network_Change_Reciever network_change_reciever;
    EditText editText;
    CheckBox saveDetailCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialise();
        getSupportActionBar().setTitle("Enter Your Details");
    }

    private void setUpBroadcastReciever()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        network_change_reciever=new Network_Change_Reciever();
        registerReceiver(network_change_reciever, filter);
        Network_Change_Reciever.setNetworkChange(this);
    }

    public void setConnectionStuff()
    {
        if(!isConnected())
        {
            next.setEnabled(false);
            isWifiConnected.setText("No");
        }
        else {
            next.setEnabled(true);
            isWifiConnected.setText("Yes");
        }
    }


    private void initialise()
    {
        isWifiConnected=(TextView)findViewById(R.id.textView4);
        next=(Button)findViewById(R.id.button);
        editText=(EditText)findViewById(R.id.editText);
        saveDetailCheck=(CheckBox)findViewById(R.id.checkBox2);
        setConnectionStuff();
        setUpBroadcastReciever();
        setEditText();
    }

    public boolean isConnected(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            return true;
        }
        return false;
    }

    public void setEditText()
    {
        if(NamePreference.getName(this)!=null)
        {
            editText.setText(NamePreference.getName(this));
        }
    }

    public void startOther(View v)
    {
        String userName=editText.getText().toString();
        if(userName.isEmpty())
        {

        }else
        {
            if(saveDetailCheck.isChecked())
            {
                NamePreference.setName(this,userName);
            }
            Intent intent=new Intent(this,ServerDetails.class);
            startActivity(intent);

        }
    }

    @Override
    public void WifiStateChanged() {
        setConnectionStuff();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Network_Change_Reciever.setNetworkChange(this);
    }
}
