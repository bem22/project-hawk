package com.example.myapplication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.util.Log;

import static java.lang.Thread.sleep;


public class UDPClient extends AsyncTask<String, String, Void> {
    private DatagramSocket udpSocket;
    private InetAddress serverAddr;
    private boolean idle = true;
    private String packet = "";

    @Override
    protected Void doInBackground(String... strings) {

        try {
            udpSocket = new DatagramSocket(6000);
            serverAddr = InetAddress.getByName("192.168.0.20");
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            while(idle) {
                byte[] buf = packet.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length,serverAddr, 5000);

                udpSocket.send(packet);
                sleep(1);
            }

            while(!idle) {
                sleep(1, 100);
            }

        } catch (IOException e) {
            Log.e("Udp Send:", "IO Error:", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setPacket(String s) {
        this.packet = s;
    }

    public void setIdle(boolean idle) {
        this.idle = idle;
    }
}
