package com.example.myapplication;

import android.os.AsyncTask;
import android.view.KeyEvent;

import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

class NetworkManager {
    private TCPClient tcpClient;
    private TCPListener tcpListener;
    private UDPClient udpClient;
    private String ipAddress;
    private Socket socket;

    private ViewUtils views;
    private RemoteState state;
    private static Packetizer packetizer = new Packetizer();
    private ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<String>(1500);

    NetworkManager(String ipAddress, ViewUtils views, RemoteState state) {
        this.ipAddress = ipAddress;
        this.views = views;
        this.state = state;
    }

    void startConnection() {
        socket = new Socket();

        udpClient = new UDPClient(this.ipAddress);
        udpClient.sendPackets();

        tcpClient = new TCPClient(messages, ipAddress, views, state, socket);
        tcpClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        tcpListener = new TCPListener(socket, state);
        tcpListener.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    void addTCPPacket(int keyCode) {
        messages.offer(getPacket(state, keyCode));
    }

    void setUDPPayload() {
        udpClient.setPacketString(getAxesPacket(state));
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
                return packetizer.packetize("LAND");
            case KeyEvent.KEYCODE_BUTTON_Y:
                return packetizer.packetize("TELE");
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
            return packetizer.packetize("STM ", state.axes_string);
        }

        else return "";
    }

}