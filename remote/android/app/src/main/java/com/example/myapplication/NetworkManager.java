package com.example.myapplication;

import android.os.AsyncTask;

import java.util.concurrent.ArrayBlockingQueue;

class NetworkManager {
    private TCPClient tcpClient;
    private UDPClient udpClient;


    private ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<String>(1500);

    NetworkManager() {
        tcpClient = new TCPClient(messages);
        udpClient = new UDPClient();
        tcpClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        udpClient.sendPackets();
    }

    void addTCPPacket(String s) {
        messages.offer(s);
    }
    void setUDPPacket(String s) {
        udpClient.setPacket(s);
    }

    void closeConnections() {
        udpClient.getSenderHandle().cancel(true);
        tcpClient.disconnect();
    }
}