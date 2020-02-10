package com.example.myapplication;

import android.os.AsyncTask;

import java.util.concurrent.ArrayBlockingQueue;

class NetworkManager {
    private TCPClient tcpClient;
    private UDPClient udpClient;
    private String ipAddress;
    private ViewUtils views;
    private RemoteState state;
    private ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<String>(1500);


    NetworkManager(String ipAddress, ViewUtils views, RemoteState state) {
        this.ipAddress = ipAddress;
        this.views = views;
        this.state = state;
        udpClient = new UDPClient(this.ipAddress);
    }

    void startConnection() {
        udpClient.sendPackets();
        tcpClient = new TCPClient(messages, ipAddress, views, state);
        tcpClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    void addTCPPacket(String s) {
        messages.offer(s);
    }

    void setUDPPayload(String s) {
        udpClient.setPacket(s);
    }

    void closeConnections() {
        udpClient.getSenderHandle().cancel(true);
        udpClient.closeSocket();
        tcpClient.cancel(true);
    }

    void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }


}