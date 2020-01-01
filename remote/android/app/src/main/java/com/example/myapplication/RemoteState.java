package com.example.myapplication;

public class RemoteState {
    private boolean armed;
    private boolean inFlight;
    private boolean connected;

    public void setArmed(boolean armed) {
        this.armed = armed;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void setInFlight(boolean inFlight) {
        this.inFlight = inFlight;
    }

    public boolean isArmed() {
        return armed;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isInFlight() {
        return inFlight;
    }
}