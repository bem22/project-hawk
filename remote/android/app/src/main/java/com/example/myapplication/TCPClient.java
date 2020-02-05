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
    TCPClient(ArrayBlockingQueue<String> messages) {
        this.messages = messages;
    }

    @Override
    protected Void doInBackground(String... strings) {
        Thread.currentThread().setName("TCP Client");
        try {
            //here you must put your computer's IP address.
            //server IP address
            String SERVER_IP = "192.168.0.20";
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

            Log.d("TCP Client", "C: Connecting...");

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
                    //sends the message to the server
                    // used to send messages
                    //receives the message which the server sends back
                    mBufferOut.println(messages.take());
                    mBufferOut.flush();
                }
            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
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
