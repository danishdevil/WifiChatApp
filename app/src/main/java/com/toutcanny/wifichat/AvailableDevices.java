package com.toutcanny.wifichat;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.toutcanny.wifichat.Data_Model.DeviceDetail;
import com.toutcanny.wifichat.Data_Model.DeviceList;
import com.toutcanny.wifichat.Helper.NamePreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class AvailableDevices extends AppCompatActivity {


    private DeviceList availableDevices;
    private String message;
    ListView listView;
    ArrayAdapter<String> deviceList;
    String serverIP,serverPort;
    String myIP,myName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available__devices);
        initialise();
        listView.setAdapter(deviceList);
        DeviceListGetter deviceListGetter=new DeviceListGetter();
        deviceListGetter.execute("hello");
    }


    //Set all the variables
    public void initialise()
    {
        message="";
        serverIP=getIntent().getStringExtra("ip_address");
        serverPort=getIntent().getStringExtra("port");
        availableDevices=new DeviceList();
        deviceList=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,availableDevices.getNames());
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
            deviceList=new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1,availableDevices.getNames());
            listView.setAdapter(deviceList);
            setMessage(message);
        }


        //Perform the Network task to send request and read stuff
        @Override
        protected String doInBackground(String... params) {
            try {
                while(true) {
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

                    //Sleep for 3 Seconds
                    Thread.sleep(3000);


            }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }


}
