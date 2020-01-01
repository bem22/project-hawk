package utils;

import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;

import com.example.myapplication.Packetizer;
import com.example.myapplication.RemoteState;

import java.util.ArrayList;
import java.util.Arrays;

public class PadUtils {
    private static Packetizer packetizer = new Packetizer();
    private ArrayList<String> axes_string = new ArrayList<>(Arrays.asList("", "", "", "", "", ""));

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

    public static String getPacket(RemoteState s, int keyCode, ArrayList args) {
        return "";
    }

    /**
     * @param state the state of the remote
     * @param args arguments of the packet (axes values)
     * @return a string containing the the payload to be sent to the drone
     */
    public String getAxesPacket(RemoteState state, ArrayList<Float> args) {
        if(!state.isArmed()) {
            axes_string.set(0, "X:" + (int)(1500 + (500 * args.get(0))));
            axes_string.set(1, "Y:" + (int)(1500 + (500 * args.get(1))));
            axes_string.set(2, "Z:" + (int)(1500 + (500 * args.get(2))));
            axes_string.set(3, "W:" + (int)(1500 + (500 * args.get(3))));
            axes_string.set(4, "A:" + (int)(1000 * (1 + args.get(4))));
            axes_string.set(5, "B:" + (int)(1000 * (1 + args.get(5))));

            return packetizer.packetize("STM ", axes_string);
        }

        else return "";
    }

}

