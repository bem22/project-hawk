package com.example.myapplication;

import android.content.Intent;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.example.myapplication.MenuActivity;
import com.example.myapplication.Packetizer;
import com.example.myapplication.RemoteState;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

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

    //TODO: Remove packetizer from here and call in networkManager, generating a callback from this call
    private static Packetizer packetizer = new Packetizer();

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

    //TODO: Refactor the function to reflect a better name
    public static String getPacket(RemoteState s, int keyCode) {
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

    /**
     * @param state the state of the remote
     * @param args arguments of the packet (axes values)
     * @return a string containing the the payload to be sent to the drone
     */
    public String getAxesPacket(RemoteState state, ArrayList<Float> args) {
        if(!state.isArmed()) {

            axes_string.set(0, "" + (int)(1000 * (1 + args.get(0))));
            axes_string.set(1, "" + (int)(1500 + (500 * args.get(1))));
            axes_string.set(2, "" + (int)(1500 + (500 * args.get(2))));
            axes_string.set(3, "" + (int)(1500 + (500 * args.get(3))));
            axes_string.set(4, "" + (int)(1500 + (500 * args.get(4))));




            axes_string.set(5, "1000");
            //axes_string.set(5, "1000");

            return packetizer.packetize("STM ", axes_string);
        }

        else return "";
    }

    public static Float getCenteredAxis(MotionEvent event,
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

