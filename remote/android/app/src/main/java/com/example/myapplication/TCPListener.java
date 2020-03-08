package com.example.myapplication;

import android.location.OnNmeaMessageListener;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TCPListener extends AsyncTask<String, Boolean, Void> {
    private RemoteState state;
    private Socket socket;
    private ViewUtils views;
    private BufferedReader mBufferIn;
    private char[] buffer = new char[1024];

    TCPListener(Socket socket, RemoteState state, ViewUtils views) {
        this.views = views;
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
            } catch (IOException ignored) {}

        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Boolean... values) {
        super.onProgressUpdate(values);

        String packet = new String(buffer);
        processPacket(packet);
    }

    private void processPacket(String packet) {
        // Tokenize the packet
        String[] tokens = packet.split("\\n");


        String chunk = tokens[1].split(":")[1].trim();
        Integer packetLength = Integer.valueOf(tokens[2].split(":")[1].trim());
        Integer numberOfParameters = Integer.valueOf(tokens[3].split(":")[1].trim());

        if(chunk.equals("BAT") && numberOfParameters == 1) {
            // Expect one parameter
            String bv = tokens[4];
            state.setBatteryVoltage(Float.parseFloat(bv));
            views.updateBatteryStatus(state.getBatteryPercentage(Double.parseDouble(bv)));
        }
    }
}
