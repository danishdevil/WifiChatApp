package com.toutcanny.wifichat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.toutcanny.wifichat.Helper.IpGetter;
import com.toutcanny.wifichat.Helper.NamePreference;
import com.toutcanny.wifichat.Helper.NetworkChange;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;

public class ChatActivity extends AppCompatActivity implements NetworkChange {

    //The other partners IP address and Name
    String ipAddress;
    String name;

    //My IP and name
    String myName;
    String myIP;

    //Server IP and Server Port
    String serverIP;
    String serverPort;

    //Should chat be started or only request to be sent?
    boolean startChat;

    //should activity run or wind up?
    boolean activityInForeground;

    //Different Views of Activity
    ListView listView;
    ArrayAdapter<String> messages;
    EditText editText;

    /*******Activities overrdden Methods******/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if(getIntent().getStringExtra("task").equals("connect"))
        {
            startChat=true;
        }else
        {
            startChat=false;
        }
        Network_Change_Reciever.setNetworkChange(this);

        initialise();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(name);
    }

    //Called when activity resumes
    @Override
    protected void onResume() {
        super.onResume();
        activityInForeground=true;
        if(startChat==false) {
            startAsyncForRequest();
        }else
        {
            startAsyncForChat();
        }
    }


    //Called when activity pause due to any reason, Finishes the activity
    @Override
    protected void onPause() {
        super.onPause();
        new MessageSender("Ø").start();
        activityInForeground=false;
        finish();
    }

    //When user presses back button
    @Override
    public void onBackPressed() {
        onPause();
    }

    /********End of overridden activities********/


    //Initialise all the variables needed
    public void initialise()
    {
        //Getting all the intent details sent by the previous activity
        ipAddress=(getIntent().getStringExtra("ipAddress"));
        name=(getIntent().getStringExtra("name"));
        serverIP=getIntent().getStringExtra("serverIP");
        serverPort=getIntent().getStringExtra("serverPort");

        //Getting name and ip of the app user
        myName= NamePreference.getName(this);
        myIP= IpGetter.getIP(this);

        //Initialise the Views in the activity
        listView=(ListView)findViewById(R.id.listView2);
        editText=(EditText)findViewById(R.id.editText4);
        messages=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        listView.setAdapter(messages);
    }

    //Start Asynctask that sends request
    public void startAsyncForRequest()
    {
        Requester requester = new Requester();
        requester.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Hello");
    }

    //Start Asynctask that is used
    public void startAsyncForChat()
    {
        Reciever rev=new Reciever();
        rev.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Hello");
        Informer informer=new Informer();
        informer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Hello");
    }


    //Called directly from the layout
    public void sendMessage(View v)
    {
        String message=editText.getText().toString();
        messages.add("You : " + message);
        new MessageSender(message).start();
        editText.setText("");
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

    class MessageSender extends Thread{

        String sender;
        public MessageSender(String s)
        {
            sender=s;
        }

        @Override
        public void run() {
            try{
                Socket socket=new Socket(ipAddress,3001);
                DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(sender);
                socket.close();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }



    //Request to connect to user.
    class Requester extends AsyncTask<String,String,String>
    {
        boolean accepted;
        ProgressDialog dialog;
        Socket socket;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ChatActivity.this, "Waiting for response", "Please wait...", true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            while(true) {
                try {
                    socket = new Socket(ipAddress, 3000);
                    int response = socket.getInputStream().read();
                    if (response == 89) {
                        accepted=true;
                        break;
                    } else {
                        accepted=false;
                        break;
                    }

                } catch (Exception e) {
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                socket.close();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            dialog.dismiss();

            if(!accepted)
            {
                finish();
            }
            startAsyncForChat();
        }
    }


    //Listens to any incomming messages.
    class Reciever extends AsyncTask<String,String,String>
    {
        String messageRead;
        ServerSocket sersocket;

        @Override
        protected void onProgressUpdate(String... values) {
            if (activityInForeground) {
                super.onProgressUpdate(values);
                if (messageRead.equals("Ø")) {
                    onPause();
                }
                messages.add(name + " : " + messageRead);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            while(activityInForeground)
            {
                try{
                    sersocket=new ServerSocket(3001);
                    sersocket.setSoTimeout(5000);
                    Socket socket=sersocket.accept();
                    DataInputStream dos=new DataInputStream((socket.getInputStream()));
                    messageRead=dos.readUTF();
                    Log.e("We got the baby",messageRead);
                    publishProgress("");
                    sersocket.close();
                }catch (SocketTimeoutException e)
                {
                    try {
                        sersocket.close();
                    } catch (IOException e1) {
                    }
                }
                catch (Exception e)
                {
                    try {
                        sersocket.close();
                    } catch (IOException e1) {
                    }
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                sersocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //Asynctask that informs the webserver about the device
    class Informer extends AsyncTask<String,String,String>
    {
        String message;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            message="";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if(activityInForeground) {
                super.onProgressUpdate(values);

                if (!message.equals(values[0])) {
                    message = values[0];
                    Toast.makeText(ChatActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            while(activityInForeground)
            {
                try{
                    String urlRequest = "?ip_address=" + myIP + "&name=" + URLEncoder.encode(myName, "UTF-8")+"&task=chat";
                    URL url = new URL("http://" + serverIP + ":" + serverPort + "/MyProject/androidInfo.php" + urlRequest);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String s;
                    StringBuffer messageString=new StringBuffer();
                    while((s=bufferedReader.readLine())!=null)
                    {
                        messageString.append(s);
                    }
                    publishProgress(messageString.toString());
                    bufferedReader.close();
                    Thread.sleep(3000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
