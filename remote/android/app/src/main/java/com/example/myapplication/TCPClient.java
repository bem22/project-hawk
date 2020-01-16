package com.example.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ArrayBlockingQueue;

public class TCPClient extends AsyncTask<String, String, Void> {
    public static final String SERVER_IP = "192.168.0.20"; //server IP address
    public static final int SERVER_PORT = 5000;

    ArrayBlockingQueue<String> messages;

    public TCPClient (ArrayBlockingQueue<String> messages) {
        this.messages = messages;
    }

    // used to read messages from the server

    @Override
    protected Void doInBackground(String... strings) {
        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

            Log.d("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVER_PORT);

            // Disable Nagle's algorithm
            socket.setTcpNoDelay(true);


            if(socket.isConnected()) {
                Log.d("TCP Client", "Connected");
            }
            try {

                while(true) {
                    //sends the message to the server
                    // used to send messages
                    PrintWriter mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                    //receives the message which the server sends back
                    mBufferOut.println(messages.take());
                    mBufferOut.flush();
                }
            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
            }

        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
        }

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
