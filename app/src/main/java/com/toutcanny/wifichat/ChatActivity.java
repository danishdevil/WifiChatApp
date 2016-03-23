package com.toutcanny.wifichat;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.toutcanny.wifichat.R;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatActivity extends AppCompatActivity {

    String ipAddress;
    boolean startChat;
    String name;
    ListView listView;
    ArrayAdapter<String> messages;
    EditText editText;
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
        ipAddress=(getIntent().getStringExtra("ipAddress"));
        name=(getIntent().getStringExtra("name"));
        initialise();
    }

    public void initialise()
    {
        listView=(ListView)findViewById(R.id.listView2);
        editText=(EditText)findViewById(R.id.editText4);
        messages=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        listView.setAdapter(messages);
    }

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
            Log.e("We Come", "Here");

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
            Reciever rev=new Reciever();
            rev.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Hello");
        }
    }

    class Reciever extends AsyncTask<String,String,String>
    {
        String messageRead;

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            messages.add(messageRead);
            Log.e("hi",""+messages.getCount());
        }

        @Override
        protected String doInBackground(String... params) {
            while(true)
            {
                try{
                    Log.e("Connect","Please Connect");
                    ServerSocket sersocket=new ServerSocket(3001);
                    Socket socket=sersocket.accept();
                    Log.e("Connect","Please Connect");
                    DataInputStream dos=new DataInputStream((socket.getInputStream()));
                    messageRead=dos.readUTF();
                    Log.e("We got the baby",messageRead);
                    publishProgress("");
                    sersocket.close();
                }catch (Exception e)
                {
                    e.printStackTrace();
                    break;
                }
            }
            return null;
        }
    }

    public void sendMessage(View v)
    {
        String message=editText.getText().toString();
        messages.add("You:" + message);
        new MessageSender(message).start();
        editText.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(startChat==false) {
            Requester requester = new Requester();
            requester.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Hello");
        }else
        {
            Reciever rev=new Reciever();
            rev.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Hello");
        }
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
                Log.e(ipAddress,"this is the ip");
                Socket socket=new Socket(ipAddress,3001);
                Log.e(socket.getRemoteSocketAddress().toString(),"");
                DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(sender);
                socket.close();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
