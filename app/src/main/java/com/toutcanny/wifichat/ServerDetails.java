package com.toutcanny.wifichat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.toutcanny.wifichat.Helper.NetworkChange;
import com.toutcanny.wifichat.Data_Model.WifiDetail;
import com.toutcanny.wifichat.WifiDatabase.WifiDetailDataSource;

public class ServerDetails extends AppCompatActivity implements NetworkChange {

    TextView ssidName;
    String ssid_name_string;
    EditText editServerIP,editServerPort;
    WifiDetailDataSource wifiDetailDataSource;
    CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_details);
        wifiDetailDataSource=new WifiDetailDataSource(this);
        inititialise();
        setSSID();
        preSetDetails();
    }

    private void inititialise()
    {
        Network_Change_Reciever.setNetworkChange(this);
        checkBox=(CheckBox)findViewById(R.id.checkBox);
        ssidName=(TextView)findViewById(R.id.textView9);
        editServerIP=(EditText)findViewById(R.id.editText2);
        editServerPort=(EditText)findViewById(R.id.editText3);
    }

    private void setSSID()
    {
        WifiManager wifiManager = (WifiManager) getSystemService (Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo ();
        ssid_name_string=info.getSSID();
        ssidName.setText(ssid_name_string);
    }

    @Override
    public void WifiStateChanged() {
        AlertDialog.Builder alertBuilder=new AlertDialog.Builder(this);
        alertBuilder.setTitle("Wifi Disconnected");
        alertBuilder.setMessage("You are now disconnected");
        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(getBaseContext(),MainActivity.class);
                startActivity(intent);
            }
        });
        alertBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Intent intent=new Intent(getBaseContext(),MainActivity.class);
                startActivity(intent);
            }
        });
        alertBuilder.show();
    }

    public void preSetDetails()
    {
        WifiDetail wifiDetail=wifiDetailDataSource.getWifiDetails(ssid_name_string);
        if(wifiDetail==null)
            return;
        editServerIP.setText(wifiDetail.getIP_Address());
        editServerPort.setText(wifiDetail.getPort());
    }

    public void onNext(View v)
    {
        if(checkBox.isChecked()) {
            wifiDetailDataSource.storeWifiDetails(new WifiDetail(ssid_name_string, editServerIP.getText().toString(), editServerPort.getText().toString()));
        }
        Intent intent=new Intent(this,AvailableDevices.class);
        intent.putExtra("ip_address",editServerIP.getText().toString());
        intent.putExtra("port", editServerPort.getText().toString());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Network_Change_Reciever.setNetworkChange(this);
    }
}
