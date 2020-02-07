package com.example.myapplication;

import android.os.AsyncTask;

import java.util.concurrent.ArrayBlockingQueue;

class NetworkManager {
    private TCPClient tcpClient;
    private UDPClient udpClient;
    private String ipAddress = "127.0.0.1"; // This address will lead to nowhere

    private ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<String>(1500);

    NetworkManager(String ipAddress) {
        this.ipAddress = ipAddress;
        tcpClient = new TCPClient(messages, this.ipAddress);
        udpClient = new UDPClient(this.ipAddress);
    }

    boolean startConnection() {
        tcpClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        udpClient.sendPackets();
        return true;
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
        tcpClient.disconnect();
    }

    void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}