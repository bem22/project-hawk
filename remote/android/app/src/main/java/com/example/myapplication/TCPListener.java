package com.example.myapplication;

import android.location.OnNmeaMessageListener;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;

public class TCPListener extends AsyncTask<String, Boolean, Void> {
    RemoteState state;
    Socket socket;
    private BufferedReader mBufferIn;
    private char[] buffer = new char[1024];

    TCPListener(Socket socket, RemoteState state) {
        this.socket = socket;
        this.state = state;
    }

    @Override
    protected Void doInBackground(String... strings) {
        while(!state.getConnectionStatus()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(state.getConnectionStatus()) {
            try {
                mBufferIn.read(buffer);
                publishProgress();
                Log.d("Message from server:", String.valueOf(buffer));
            } catch (IOException e) {

            }

        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Boolean... values) {
        super.onProgressUpdate(values);

        String packet = Arrays.toString(buffer);

        if(packet.contains("BAT")) {
            Log.d("Hello", "WORLD");
        }

    }
}
