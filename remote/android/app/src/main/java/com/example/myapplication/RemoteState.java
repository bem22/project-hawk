package com.example.myapplication;

public class RemoteState {

    RemoteState() {
        this.gain = 100;
        this.minGain = 1;
        this.maxGain = 10000;

        this.throttle = 1000;
        this.minThrottle = 1000;
        this.maxThrottle = 2000;
    }

    private boolean connectionStatus = false;

    private int gainCurve;

    private int gain;
    private int minGain;
    private int maxGain;


    private int throttle;
    private int minThrottle;
    private int maxThrottle;


    private boolean armingStatus;

    private boolean trimmer;

    private boolean flightStatus;

    private int throttleTrimmerAdvancedLevel;

    private boolean throttleTrimmerAdvancedMode;

    private int throttleTrimmerBasicLevel;

    private boolean throttleTrimmerBasicMode;

    private float yawTrimmerLevel;
    private float maxYawTrimmerLevel;
    private float minYawTrimmerLevel;

    private float pitchTrimmerLevel;
    private float maxPitchTrimmerLevel;
    private float minPitchTrimmerLevel;

    private float rollTrimmerLevel;
    private float maxRollTrimmerLevel;
    private float minRollTrimmerLevel;

    private float throttleTrimmerLevel;
    private float maxThrottleTrimmerLevel;
    private float minThrottleTrimmerLevel;


    public int getThrottleTrimmerBasicLevel() {
        return throttleTrimmerBasicLevel;
    }

    public float getYawTrimmerLevel() {
        return getTrimmerLevel(yawTrimmerLevel, minYawTrimmerLevel,  maxYawTrimmerLevel);
    }

    public float getMaxYawTrimmerLevel() {
        return maxYawTrimmerLevel;
    }

    public float getMinYawTrimmerLevel() {
        return minYawTrimmerLevel;
    }

    public float getPitchTrimmerLevel() {
        return getTrimmerLevel(pitchTrimmerLevel, minPitchTrimmerLevel, maxPitchTrimmerLevel);
    }

    public float getMaxPitchTrimmerLevel() {
        return maxPitchTrimmerLevel;
    }

    public float getMinPitchTrimmerLevel() {
        return minPitchTrimmerLevel;
    }

    public float getRollTrimmerLevel() {
        return getTrimmerLevel(rollTrimmerLevel, minRollTrimmerLevel, maxRollTrimmerLevel);
    }

    public float getMaxRollTrimmerLevel() {
        return maxRollTrimmerLevel;
    }

    public float getMinRollTrimmerLevel() {
        return minRollTrimmerLevel;
    }

    public float getThrottleTrimmerLevel() {
        return getTrimmerLevel(throttleTrimmerLevel, minThrottleTrimmerLevel, maxThrottleTrimmerLevel);
    }

    public float getMaxThrottleTrimmerLevel() {
        return maxThrottleTrimmerLevel;
    }

    public float getMinThrottleTrimmerLevel() {
        return minThrottleTrimmerLevel;
    }

    public boolean isArmed() {
        return armingStatus;
    }

    public void setArmingStatus(boolean armingStatus) {
        this.armingStatus = armingStatus;
    }

    public void setFlightStatus(boolean flightStatus) {
        this.flightStatus = flightStatus;
    }

    void setTrimmer(boolean trimmer) {
        this.trimmer = trimmer;
    }

    public boolean getTrimmer() {
        return this.trimmer;
    }

    public void setYawTrimmerLevel(float yawTrimmerLevel) {
        this.yawTrimmerLevel = yawTrimmerLevel;
    }

    public void setMaxYawTrimmerLevel(float maxYawTrimmerLevel) {
        this.maxYawTrimmerLevel = maxYawTrimmerLevel;
    }

    public void setMinYawTrimmerLevel(float minYawTrimmerLevel) {
        this.minYawTrimmerLevel = minYawTrimmerLevel;
    }

    public void setPitchTrimmerLevel(float pitchTrimmerLevel) {
        this.pitchTrimmerLevel = pitchTrimmerLevel;
    }

    public void setMaxPitchTrimmerLevel(float maxPitchTrimmerLevel) {
        this.maxPitchTrimmerLevel = maxPitchTrimmerLevel;
    }

    public void setMinPitchTrimmerLevel(float minPitchTrimmerLevel) {
        this.minPitchTrimmerLevel = minPitchTrimmerLevel;
    }

    public void setRollTrimmerLevel(float rollTrimmerLevel) {
        this.rollTrimmerLevel = rollTrimmerLevel;
    }

    public void setMaxRollTrimmerLevel(float maxRollTrimmerLevel) {
        this.maxRollTrimmerLevel = maxRollTrimmerLevel;
    }

    public void setMinRollTrimmerLevel(float minRollTrimmerLevel) {
        this.minRollTrimmerLevel = minRollTrimmerLevel;
    }

    public void setThrottleTrimmerLevel(float throttleTrimmerLevel) {
        this.throttleTrimmerLevel = throttleTrimmerLevel;
    }

    public void setMaxThrottleTrimmerLevel(float maxThrottleTrimmerLevel) {
        this.maxThrottleTrimmerLevel = maxThrottleTrimmerLevel;
    }

    public void setMinThrottleTrimmerLevel(float minThrottleTrimmerLevel) {
        this.minThrottleTrimmerLevel = minThrottleTrimmerLevel;
    }


    // This function is  what the actual trimmingLevel getters call in order to return correct trimming levels within boundaries.
    private float getTrimmerLevel(float trimmingLevel, float minTrimmingLevel, float maxTrimmingLevel) {
        if(trimmingLevel > maxTrimmingLevel) {
            return maxTrimmingLevel;
        } else if(trimmingLevel < minTrimmingLevel) {
            return minTrimmingLevel;
        } else return trimmingLevel;
    }


    public int getGainCurve() {
        return gainCurve;
    }

    public void setGainCurve(int gainCurve) {
        this.gainCurve = gainCurve;
    }

    void increaseGain() {
        setGain(gain*2);
    }

    void decreaseGain() {
        setGain(gain/2);
    }

    int getGain() {
        return gain;
    }

    private void setGain(int gain) {
        if(gain > maxGain) {
            this.gain = maxGain;
        } else if(gain < minGain) {
            this.gain = minGain;
        } else {
            this.gain = gain;
        }
    }

    public int getMinGain() {
        return minGain;
    }

    public void setMinGain(int minGain) {
        this.minGain = minGain;
    }

    public int getMaxGain() {
        return maxGain;
    }

    public void setMaxGain(int maxGain) {
        this.maxGain = maxGain;
    }

    void increaseThrottle() {
        setThrottle(throttle + gain);
    }

    void decreaseThrottle() {
        setThrottle(throttle - gain);
    }

    int getThrottle() {
        return throttle;
    }

    private void setThrottle(int throttle) {
        if(throttle > maxThrottle) {
            this.throttle = maxThrottle;
        } else if(throttle < minThrottle) {
            this.throttle = minThrottle;
        } else {
            this.throttle = throttle;
        }
    }

    public int getMinThrottle() {
        return minThrottle;
    }

    public void setMinThrottle(int minThrottle) {
        this.minThrottle = minThrottle;
    }

    public int getMaxThrottle() {
        return maxThrottle;
    }

    public void setMaxThrottle(int maxThrottle) {
        this.maxThrottle = maxThrottle;
    }

    public boolean getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionState(boolean connected) {
        connectionStatus = connected;
    }

}
