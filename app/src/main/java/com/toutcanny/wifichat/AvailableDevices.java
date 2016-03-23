package com.toutcanny.wifichat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.toutcanny.wifichat.Data_Model.DeviceList;
import com.toutcanny.wifichat.Helper.NamePreference;
import com.toutcanny.wifichat.Helper.NetworkChange;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;

public class AvailableDevices extends AppCompatActivity implements AdapterView.OnItemClickListener,NetworkChange {


    private DeviceList availableDevices;
    private String message;
    ListView listView;
    ArrayAdapter<String> deviceList;
    String serverIP,serverPort;
    String myIP,myName;
    DeviceListGetter deviceListGetter;
    ChatRequestListener chatRequestListener;

    boolean ActivityInForeground;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available__devices);
        initialise();
        listView.setAdapter(deviceList);
        listView.setOnItemClickListener(this);
        Network_Change_Reciever.setNetworkChange(this);
    }

    //Start the Asynctasks
    private void startAsynctasks()
    {
        deviceListGetter=new DeviceListGetter();
        deviceListGetter.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"Hello");
        chatRequestListener=new ChatRequestListener();
        chatRequestListener.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"Hello");
    }


    //Set all the variables
    public void initialise()
    {
        message="";
        serverIP=getIntent().getStringExtra("ip_address");
        serverPort=getIntent().getStringExtra("port");
        availableDevices=new DeviceList();
        deviceList=new ArrayAdapter<String>(this,R.layout.available_device_list_item,R.id.textView10,availableDevices.getNames());
        listView=(ListView)findViewById(R.id.listView);
        setMyDeviceDetail();
    }

    //Getting and setting Wifi Application name
    public void setMyDeviceDetail()
    {
        myName= NamePreference.getName(this);
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        myIP=ip;
    }


    //Display the message toast
    public void setMessage(String message)
    {
        if(!this.message.equals(message))
        {
            this.message=message;
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        }
    }


    //Item selected listener
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Connect to "+availableDevices.getName(position)+"?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(getBaseContext(),ChatActivity.class);
                intent.putExtra("task","request");
                intent.putExtra("ipAddress",availableDevices.getIpAddresses().get(position));
                intent.putExtra("name",availableDevices.getNames().get(position));
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No",null);
        builder.create().show();
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


    //Listen for devices trying to connect
    class ChatRequestListener extends AsyncTask<String,Socket,String>
    {
        String name;
        String ip;
        ServerSocket sersock;

        @Override
        protected void onProgressUpdate(Socket... values) {
            super.onProgressUpdate(values);

            final Socket socket=values[0];
            StringBuilder stringBuilder=new StringBuilder(socket.getInetAddress().toString());
            stringBuilder.deleteCharAt(0);
            ip=stringBuilder.toString();
            name=availableDevices.getName(ip);

            AlertDialog.Builder builder=new AlertDialog.Builder(AvailableDevices.this);
            builder.setTitle("Connect to " + name + "?");
            builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        socket.getOutputStream().write("Y".getBytes());
                        socket.close();
                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                        intent.putExtra("ipAddress", ip);
                        intent.putExtra("name",name);
                        intent.putExtra("task","connect");
                        sersock.close();
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        socket.getOutputStream().write("N".getBytes());
                        socket.close();
                    }catch (Exception e)
                    {

                    }
                }
            });
            builder.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try{
                while(!isCancelled()) {
                    //Opening the server socket to listen
                    sersock = new ServerSocket(3000);
                    Log.e("pin",sersock.getInetAddress().toString());
                    Socket socket=sersock.accept();
                    publishProgress(socket);
                    sersock.close();
                }
            }catch (Exception e)
            {

            }
            return null;
        }
    }



    //Asynctask Task That performs the refresh stuff
    class DeviceListGetter extends AsyncTask<String,String,String>
    {
        String message;

        //Read JSON String and set all the Variables
        private void JsonExtractor (String s) throws JSONException {

            //Reading the String and forming the JSONObject
            JSONObject mainBody=new JSONObject(s);

            //Reading Message
            message=mainBody.getString("message");
            JSONArray devices=mainBody.getJSONArray("connected_devices");
            for(int i=0;i<devices.length();++i)
            {
                JSONObject device=(JSONObject)devices.get(i);
                availableDevices.addItem(device.getString("ip"),device.getString("name"));
                device=new JSONObject();
            }

        }

        //Set the Device List
        @Override
        protected void onProgressUpdate(String... progress) {

            //Updating the adapters
            deviceList=new ArrayAdapter<String>(getBaseContext(),R.layout.available_device_list_item,R.id.textView10,availableDevices.getNames());
            listView.setAdapter(deviceList);
            setMessage(message);
        }


        //Perform the Network task to send request and read stuff
        @Override
        protected String doInBackground(String... params) {
            try {
                while(ActivityInForeground) {
                    //Forming the Request
                    String urlRequest = "?ip_address=" + myIP + "&name=" + URLEncoder.encode(myName, "UTF-8")+"&task=search";
                    URL url = new URL("http://" + serverIP + ":" + serverPort + "/MyProject/androidInfo.php" + urlRequest);

                    //Opening Connection
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    //Getting String and Reading
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String s;
                    StringBuffer jsonString=new StringBuffer();
                    while((s=bufferedReader.readLine())!=null)
                    {
                        jsonString.append(s);
                    }

                    //Using the read String
                    JsonExtractor(jsonString.toString());
                    publishProgress("Calling the UI Thread");

                    //Closing the Links
                    bufferedReader.close();
                    httpURLConnection.disconnect();

                    //Sleep for 3 Seconds
                    Thread.sleep(3000);


            }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }


    //Resuming the Asynctasks
    @Override
    protected void onResume() {
        super.onResume();
        ActivityInForeground=true;
        startAsynctasks();
        Network_Change_Reciever.setNetworkChange(this);
    }


    //Stopping the Asynctasks
    @Override
    protected void onPause() {
        super.onPause();
        ActivityInForeground=false;
        deviceListGetter.cancel(true);
        chatRequestListener.cancel(true);
    }
}
