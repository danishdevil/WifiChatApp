package com.toutcanny.wifichat;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.toutcanny.wifichat.R;

import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;

public class ChatActivity extends AppCompatActivity {

    String ipAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ipAddress=(getIntent().getStringExtra("ipAddress"));
        Requester requester=new Requester();
        requester.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"Hello");
    }

    class Requester extends AsyncTask<String,String,String>
    {
        boolean accepted;
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ChatActivity.this, "Waiting for response", "Please wait...", true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.e("We Come","Here");
            while(true) {
                try {
                    Socket socket = new Socket(ipAddress, 3000);
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
            dialog.dismiss();
            if(!accepted)
            {
                finish();
            }
        }
    }

}
