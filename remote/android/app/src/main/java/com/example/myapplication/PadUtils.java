package com.example.myapplication;

import android.view.InputDevice;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class PadUtils {

    /** This variable holds the updated values from all 6 axes on the sticks/shoulders
     * axes[0] is THROTTLE
     * axes[1] is ROLL
     * axes[2] is PITCH
     * axes[3] is YAW
     * axes[4] is AUX1
     * axes[5] is AUX2
     */
    private ArrayList<Float> rawAxes = new ArrayList<>(Arrays.asList((float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0));

    public ArrayList<Float> getAxes() {
        return rawAxes;
    }

    private ArrayList<String> axes_string = new ArrayList<>(Arrays.asList("", "", "", "", "", ""));

    //TODO: Use this call to see if there's a controller connected.
    public static ArrayList<Integer> getGameControllerIds() {
        ArrayList<Integer> gameControllerDeviceIds = new ArrayList<Integer>();
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int deviceId : deviceIds) {
            InputDevice dev = InputDevice.getDevice(deviceId);
            int sources = dev.getSources();

            // Verify that the device has gamepad buttons, control sticks, or both.
            if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
                    || ((sources & InputDevice.SOURCE_JOYSTICK)
                    == InputDevice.SOURCE_JOYSTICK)) {
                // This device is a game controller. Store its device ID.
                if (!gameControllerDeviceIds.contains(deviceId)) {
                    gameControllerDeviceIds.add(deviceId);
                }
            }
        }
        return gameControllerDeviceIds;
    }


    static Float getCenteredAxis(MotionEvent event,
                                 InputDevice device, int axis, int historyPos) {
        final InputDevice.MotionRange range =
                device.getMotionRange(axis, event.getSource());

        // A joystick at rest does not always report an absolute position of
        // (0,0). Use the getFlat() method to determine the range of values
        // bounding the joystick axis center.
        if (range != null) {
            final float flat = range.getFlat();
            final float value =
                    historyPos < 0 ? event.getAxisValue(axis):
                            event.getHistoricalAxisValue(axis, historyPos);

            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0.0F;
    }

}

