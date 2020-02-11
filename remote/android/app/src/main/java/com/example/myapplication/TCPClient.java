package com.example.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

public class TCPClient extends AsyncTask<String, Boolean, Void> {
    private boolean connected = false;
    private ArrayBlockingQueue<String> messages;
    private String SERVER_IP;
    private ViewUtils views;
    private RemoteState state;


    TCPClient(ArrayBlockingQueue<String> messages, String ipAddress, ViewUtils views, RemoteState state) {
        this.messages = messages;
        this.SERVER_IP = ipAddress;
        this.views = views;
        this.state = state;
    }

    @Override
    protected Void doInBackground(String... strings) {
        Thread.currentThread().setName("TCP Client");
        try {
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
            
            //create a socket to make the connection with the server
            int SERVER_PORT = 5000;

            Socket socket = new Socket();

            //TODO: Check what kind of timeout I shall be looking for
            //socket.setSoTimeout(200);
            socket.connect(new InetSocketAddress(serverAddr, SERVER_PORT), 200);
            PrintWriter mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            // Disable Nagle's algorithm
            socket.setTcpNoDelay(true);

            if(socket.isConnected()) {
                connected = true;
                views.showConnectionIcon(true);
                state.setConnectionState(true);
            }
            try {
                while (connected) {
                    mBufferOut.println(messages.take());
                    mBufferOut.flush();
                }
            } catch(InterruptedException e) {
                Log.d("Interrupted", " messages.take()");
            } finally {
                connected = false;
                mBufferOut.close();
                socket.close();this.cancel(true);
            }

        } catch (IOException e) {
            connected = false;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void Void) {
        super.onPostExecute(Void);
        views.showConnectionIcon(false);
        state.setConnectionState(false);
        this.cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        views.showConnectionIcon(false);
        state.setConnectionState(false);
    }

}
