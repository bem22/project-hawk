package com.example.myapplication;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import utils.PadUtils;

public class MainActivity extends Activity {

    CircleView circleLeft;
    CircleView circleRight;

    RemoteState state = new RemoteState();
    PadUtils gamepad = new PadUtils();

    ArrayList<Float> axes = new ArrayList<>(Arrays.asList((float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0));
    int[] locationsL = new int[2];
    int[] locationsR = new int[2];

    static TCPClient tcp;
    NetworkManager net = new NetworkManager(tcp);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        circleLeft = findViewById(R.id.circle_left);
        circleRight = findViewById(R.id.circle_right);
        net.execute();
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        circleLeft.getLocationInWindow(locationsL);
        circleRight.getLocationInWindow(locationsR);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                == InputDevice.SOURCE_GAMEPAD) {
            if (event.getRepeatCount() == 0) {
                net.addPacket(PadUtils.getPacket(state, keyCode));
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
                event.getAction() == MotionEvent.ACTION_MOVE && (event.getSource() & InputDevice.SOURCE_DPAD) != InputDevice.SOURCE_DPAD){

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

    private static Float getCenteredAxis(MotionEvent event,
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


    private void processJoystickInput(MotionEvent event,
                                      int historyPos) {
        InputDevice inputDevice = event.getDevice();

        // Calculate the horizontal distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat axis, or the right control stick.
        axes.set(0, getCenteredAxis(event, inputDevice, MotionEvent.AXIS_X, historyPos));
        if (axes.get(0) == 0) {
            axes.set(0, getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_X, historyPos));
        }

        // Calculate the vertical distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat switch, or the right control stick.
        axes.set(1, -getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Y, historyPos));
        if (axes.get(1) == 0) {
            axes.set(1, -getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_Y, historyPos));
        }

        axes.set(2, getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Z, historyPos));
        if (axes.get(2) == 0) {
            axes.set(2, getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_X, historyPos));
        }

        axes.set(3, -getCenteredAxis(event, inputDevice, MotionEvent.AXIS_RZ, historyPos));
        if (axes.get(3) == 0) {
            axes.set(3, -getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_Y, historyPos));
        }

        axes.set(4, event.getAxisValue(MotionEvent.AXIS_GAS));
        axes.set(5, event.getAxisValue(MotionEvent.AXIS_BRAKE));


        circleRight.setScaleX(1 + axes.get(4) * 3);
        circleRight.setScaleY(1 + axes.get(4) * 3);

        circleLeft.setScaleX(1 + axes.get(5) * 3);
        circleLeft.setScaleY(1 + axes.get(5) * 3);


        circleLeft.setX(locationsL[0] + axes.get(0) * 500);
        circleLeft.setY(locationsL[1] - axes.get(1) * 500);

        circleRight.setX((float)locationsR[0]/3 + axes.get(2) * 500);
        circleRight.setY(locationsR[1] - axes.get(3) * 500);

        String packet = gamepad.getAxesPacket(state, axes);

        net.addPacket(packet);

    }
}
