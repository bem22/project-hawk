package com.example.myapplication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.util.Log;

class UDPClient {

    private DatagramSocket udpSocket;
    private InetAddress serverAddr;
    private String packetString = "";
    private ScheduledFuture senderHandle;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private String ipAddress;

    UDPClient(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    private final Runnable sender = new Runnable() {
        public void run() {
            try {
                byte[] buf = packetString.getBytes();
                //Log.d(packetString, " from UDPclient");
                DatagramPacket packet = new DatagramPacket(buf, buf.length,serverAddr, 5000);
                udpSocket.send(packet);

            } catch (IOException e) {
                Log.e("Udp Send:", "IO Error:", e);
            }
        }
    };
    void sendPackets() {
        try {
            udpSocket = new DatagramSocket(6000);
            serverAddr = InetAddress.getByName(ipAddress);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        senderHandle =
                scheduler.scheduleWithFixedDelay(sender, 0, 5, TimeUnit.MILLISECONDS);
    }

    void setPacketString(String s) {
        this.packetString = s;
    }

    void closeSocket() {
        udpSocket.close();
    }

    ScheduledFuture getSenderHandle() {
        return senderHandle;
    }
}
