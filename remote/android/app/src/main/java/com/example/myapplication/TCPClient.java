package com.example.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;

public class TCPClient extends AsyncTask<String, Boolean, Void> {
    private boolean connected = false;
    private ArrayBlockingQueue<String> messages;
    private String SERVER_IP;
    private ViewUtils views;
    private RemoteState state;
    private Socket socket;
    private int SERVER_PORT;

    TCPClient(ArrayBlockingQueue<String> messages, String ipAddress, ViewUtils views, RemoteState state, Socket socket) {
        this.socket = socket;
        this.messages = messages;
        this.SERVER_PORT = 5000;
        this.SERVER_IP = ipAddress;
        this.views = views;
        this.state = state;
    }

    @Override
    protected Void doInBackground(String... strings) {

        InetAddress serverAddr = null;
        try {
            // Disable Nagle's algorithm
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(200);
            serverAddr = InetAddress.getByName(SERVER_IP);
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }

        Thread.currentThread().setName("TCP Client");
        try {
            socket.connect(new InetSocketAddress(serverAddr, SERVER_PORT), 200);
            PrintWriter mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            BufferedReader mBufferedIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
