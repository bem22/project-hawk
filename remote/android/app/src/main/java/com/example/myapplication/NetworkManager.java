package com.example.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

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

    protected boolean addMessage(String s) {
        messages.offer(s);
        return true;
    }

    protected void popMessage() {
        try {
            messages.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}