package com.example.myapplication;

import android.os.AsyncTask;
import android.util.Log;

public class NetworkManager extends AsyncTask<String, String, TCPClient> {
    private TCPClient tcp;

    NetworkManager(TCPClient tcp) {
        this.tcp = tcp;
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
        tcp.run();

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        //response received from server
        Log.d("test", "response " + values[0]);
        //process server response here....

    }
}