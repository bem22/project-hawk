package com.example.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

public class TCPClient extends AsyncTask<String, String, Void> {
    private boolean connected = true;
    private ArrayBlockingQueue<String> messages;
    private String SERVER_IP = "192.168.0.20";
    TCPClient(ArrayBlockingQueue<String> messages, String ipAddress) {
        this.messages = messages;
        this.SERVER_IP = ipAddress;
    }

    @Override
    protected Void doInBackground(String... strings) {
        Thread.currentThread().setName("TCP Client");
        try {
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
            
            //create a socket to make the connection with the server
            int SERVER_PORT = 5000;
            Socket socket = new Socket(serverAddr, SERVER_PORT);
            PrintWriter mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            // Disable Nagle's algorithm
            socket.setTcpNoDelay(true);

            if(socket.isConnected()) {
                Log.d("TCP Client", "Connected");
            }
            try {

                while(connected) {
                    mBufferOut.println(messages.take());
                    mBufferOut.flush();
                }
            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            } finally {
                mBufferOut.close();
                socket.close();
            }

        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
        }

        return null;
    }

    void disconnect() {
        this.connected = false;
    }
}
