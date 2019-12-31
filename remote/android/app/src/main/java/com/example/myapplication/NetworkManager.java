package com.example.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;

public class NetworkManager extends AsyncTask<String, String, TCPClient> {
    private TCPClient tcp;
    private ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<String>(1500);

    NetworkManager(TCPClient tcp) {
        this.tcp = tcp;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected TCPClient doInBackground(String... message) {

        //we create a TCPClient object
        tcp = new TCPClient(new TCPClient.OnMessageReceived() {
            @Override
            //here the messageReceived method is implemented
            public void messageReceived(String message) {
                //this method calls the onProgressUpdate
                publishProgress(message);
            }
        });
        tcp.run(messages);

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        //response received from server
        Log.d("test", "response " + values[0]);
        //process server response here....

    }

    void addPacket(String s) {
        messages.offer(s);
    }
}