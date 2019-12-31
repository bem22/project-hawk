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

    public String getPacket(RemoteState s, ArrayList<Float> args) {
        if(!s.isArmed()) {
            for(int i =0; i<6; i++) {
                this.axes_string.set(0, args.get(i).toString());
            }
            return packetizer.packetize("STM ", axes_string);
        }

        else return "";
    }

}

