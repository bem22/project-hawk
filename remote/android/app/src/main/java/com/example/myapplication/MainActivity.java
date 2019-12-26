package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

import utils.PadUtils;

public class MainActivity extends Activity {

    CircleView circleLeft;
    CircleView circleRight;

    int[] locationsL = new int[2];
    int[] locationsR = new int[2];

    static TCPClient tcp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        circleLeft = findViewById(R.id.circle_left);
        circleRight = findViewById(R.id.circle_right);
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        circleLeft.getLocationInWindow(locationsL);
        circleRight.getLocationInWindow(locationsR);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = false;
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                == InputDevice.SOURCE_GAMEPAD) {
            if (event.getRepeatCount() == 0) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BUTTON_A:
                        handled = true;
                        Log.d("Pressed A", "A");
                        break;
                    case KeyEvent.KEYCODE_BUTTON_B:
                        handled = true;
                        new NetworkManager(tcp).execute("");
                        break;
                    case KeyEvent.KEYCODE_BUTTON_X:
                        handled = true;
                        Log.d("Pressed X", "X");
                        break;
                    case KeyEvent.KEYCODE_BUTTON_Y:
                        handled = true;
                        Log.d("Pressed Y", "Y");
                        break;
                    case KeyEvent.KEYCODE_DPAD_UP:
                        handled = true;
                        Log.d("Pressed", "DPADUP");
                        break;
                }
            }
            if (handled) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {

        // Check that the event came from a game controller
        if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) ==
                InputDevice.SOURCE_JOYSTICK &&
                event.getAction() == MotionEvent.ACTION_MOVE) {

            // Process all historical movement samples in the batch
            final int historySize = event.getHistorySize();

            // Process the movements starting from the
            // earliest historical position in the batch
            for (int i = 0; i < historySize; i++) {
                // Process the event at historical position i
                processJoystickInput(event, i);
            }

            // Process the current movement sample in the batch (position -1)
            processJoystickInput(event, -1);
            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    private static float getCenteredAxis(MotionEvent event,
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
        return 0;
    }


    private void processJoystickInput(MotionEvent event,
                                      int historyPos) {
        InputDevice inputDevice = event.getDevice();

        // Calculate the horizontal distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat axis, or the right control stick.
        float x = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_X, historyPos);
        if (x == 0) {
            x = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_HAT_X, historyPos);
        }

        // Calculate the vertical distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat switch, or the right control stick.
        float y = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_Y, historyPos);
        if (y == 0) {
            y = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_HAT_Y, historyPos);
        }

        float z = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_Z, historyPos);
        if (z == 0) {
            z = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_HAT_X, historyPos);
        }

        float rz = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_RZ, historyPos);
        if (rz == 0) {
            rz = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_HAT_Y, historyPos);
        }
        // Update the ship object based on the new x and y values

        circleRight.setX((float)locationsR[0]/3 + z * 500);
        circleRight.setY((float)locationsR[1] + rz * 500);
        circleLeft.setX(locationsL[0] + x * 500);
        circleLeft.setY(locationsL[1] + y * 500);

        Log.d("L Locs", "" + locationsL[0] + ":" + locationsL[1]);
        Log.d("R Locs", "" + locationsR[0] + ":" + locationsR[1]);

        Log.d("LSTICK", "position" + x + "\t" + y + " \t" + z + "\t" + rz);
    }

}
