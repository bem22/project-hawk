package com.example.myapplication;

import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;

import java.util.concurrent.ArrayBlockingQueue;

class NetworkManager {
    private TCPClient tcpClient;
    private UDPClient udpClient;
    private String ipAddress;
    private ViewUtils views;
    private RemoteState state;
    private static Packetizer packetizer = new Packetizer();
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

    void addTCPPacket(int keyCode) {
        messages.offer(getPacket(state, keyCode));
    }

    void setUDPPayload(String s) {
        udpClient.setPacketString(s);
    }

    void closeConnections() {
        udpClient.getSenderHandle().cancel(true);
        udpClient.closeSocket();
        tcpClient.cancel(true);
    }

    void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    String getPacket(RemoteState s, int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BUTTON_A:
                return packetizer.packetize("ARM ");
            case KeyEvent.KEYCODE_BUTTON_B:
                return packetizer.packetize("DARM");
            case KeyEvent.KEYCODE_BUTTON_X:
                //TODO: handle state - landing
                //TODO: create args
                return packetizer.packetize("LAND");
            case KeyEvent.KEYCODE_BUTTON_Y:
                //TODO: handle state - telemetry
                //TODO: create args
                return packetizer.packetize("TELE");
            case KeyEvent.KEYCODE_BACK:
                //TODO: jump to new activity from main activity
                Log.d("Hello", "world");
                return "BACK";
            case KeyEvent.KEYCODE_BUTTON_START:
                //TODO: use this button in the new activity (state)
                Log.d("Hello", "menu");
                return "MENU";
            default:
                return "EMPTY";
        }
    }

    String getAxesPacket(RemoteState state) {
        if(!state.isArmed()) {
            state.axes_string.set(0, state.getAxes().get(0).toString());
            state.axes_string.set(1, state.getAxes().get(1).toString());
            state.axes_string.set(2, state.getAxes().get(2).toString());
            state.axes_string.set(3, state.getAxes().get(3).toString());
            state.axes_string.set(4, state.getAxes().get(4).toString());
            state.axes_string.set(5, state.getAxes().get(5).toString());
            state.axes_string.set(6, state.getAxes().get(6).toString());
            state.axes_string.set(7, state.getAxes().get(7).toString());
            Log.d(packetizer.packetize("STM ", state.axes_string), " ");
            return packetizer.packetize("STM ", state.axes_string);
        }

        else return "";
    }

}