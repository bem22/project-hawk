package com.example.myapplication;

import android.view.KeyEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class RemoteState {

    private final short FLIGHT_MODE_ACRO = 1000;
    private final short FLIGHT_MODE_ANGLE = 1500;
    private final short FLIGHT_MODE_HORIZON = 2000;

    private final short MAGNETOMETER_OFF = 1000;
    private final short MAGNETOMETER_ON = 1500;

    private final short BAROMETER_OFF = 1000;
    private final short BAROMETER_ON = 1500;
    private boolean leftThumbState;
    private boolean rightThumbState;

    RemoteState() {
        this.gain = 100;
        this.minGain = 1;
        this.maxGain = 1000;

        this.throttle = 1000;
        this.minThrottle = 1000;
        this.maxThrottle = 2000;
    }

    /** This variable holds the updated values from all 6 axes on the sticks/shoulders
     * axes[0] is THROTTLE
     * axes[1] is ROLL
     * axes[2] is PITCH
     * axes[3] is YAW
     * axes[4] is AUX1
     * axes[5] is AUX2
     * axes[6] is AUX3
     * axes[7] is AUX4
     */

    // This will store 8 values ranging from 1000 to 2000
    private ArrayList<Integer> axes = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0));

    /** This array holds the updated raw values  from all six axes from the sticks/shoulders
     * rawAxes[0] = left stick
     */
    private ArrayList<Float> rawAxes = new ArrayList<>(Arrays.asList((float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0));

    ArrayList<String> axes_string = new ArrayList<>(Arrays.asList("", "", "", "", "", "", "", ""));

    ArrayList<Float> getRawAxes() {
        return rawAxes;
    }

    private int flightMode = FLIGHT_MODE_ANGLE;
    private int barometerStatus = BAROMETER_OFF;
    private int magnetometerStatus= MAGNETOMETER_OFF;

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

    boolean getConnectionStatus() {
        return connectionStatus;
    }

    void setConnectionState(boolean connected) {
        connectionStatus = connected;
    }

    ArrayList<Integer> getAxes() {
        this.setAxes(this.rawAxes);
        return axes;
    }

    void setFlightMode(String flightMode) {
        switch(flightMode) {
            case "ANGLE":
                this.flightMode = FLIGHT_MODE_ANGLE;
                break;
            case "HORIZON":
                this.flightMode = FLIGHT_MODE_HORIZON;
                break;
            case "ACRO":
                this.flightMode = FLIGHT_MODE_ACRO;
                break;
            default:
                break;
        }
    }

    void setBarometerStatus(boolean barometerStatus) {
        if(barometerStatus) {
            this.barometerStatus = BAROMETER_ON;

        } else {
            this.barometerStatus = BAROMETER_OFF;
        }
    }

    void setMagnetometerStatus(boolean magnetometerStatus) {
        if(magnetometerStatus) {
            this.magnetometerStatus = MAGNETOMETER_ON;
        } else {
            this.magnetometerStatus = MAGNETOMETER_OFF;
        }
    }

    private void setAxes(ArrayList<Float> rawAxes) {
        this.axes.set(0, (int)(1000 * (1 + rawAxes.get(0))));
        this.axes.set(1, (int)(1500 + (500 * rawAxes.get(1))));
        this.axes.set(2, (int)(1500 + (500 * rawAxes.get(2))));
        this.axes.set(3, (int)(1500 + (500 * rawAxes.get(3))));

        this.axes.set(4, flightMode);
        this.axes.set(5, barometerStatus);
        this.axes.set(6, magnetometerStatus);
        this.axes.set(7, 1000);
    }

    void setButtonState(int keyCode) {
        if(keyCode == KeyEvent.KEYCODE_BUTTON_THUMBL) {
            leftThumbState = true;
        }

        if(keyCode == KeyEvent.KEYCODE_BUTTON_THUMBR) {
            rightThumbState = true;
        }

        useState();
    }

    void resetButtonState(int keyCode) {
        if(keyCode == KeyEvent.KEYCODE_BUTTON_THUMBL) {
            leftThumbState = false;
        }

        if(keyCode == KeyEvent.KEYCODE_BUTTON_THUMBR) {
            rightThumbState = false;
        }
    }

    private void useState() {
        if(leftThumbState && rightThumbState) {
            barometerStatus = BAROMETER_ON;
        } else {
            barometerStatus = BAROMETER_OFF;
        }
    }
}
