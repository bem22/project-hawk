package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;
import java.util.Arrays;

import utils.PadUtils;
import utils.ViewUtils;

public class MainActivity extends Activity {


    CircleView circleLeft;
    CircleView circleRight;

    RelativeLayout rightSlate;

    RemoteState state = new RemoteState();
    PadUtils gamepad = new PadUtils();

    ArrayList<Float> axes = new ArrayList<>(Arrays.asList((float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0));

    int[] locationsL = new int[2];
    int[] locationsR = new int[2];

    NetworkManager net;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        circleLeft = findViewById(R.id.circle_left);
        circleRight = findViewById(R.id.circle_right);
        rightSlate = findViewById(R.id.rightSlate);

        net = new NetworkManager();
    }


    @Override
    protected void onDestroy() {
        net.closeConnections();
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        circleLeft.getLocationInWindow(locationsL);
        circleRight.getLocationInWindow(locationsR);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                == InputDevice.SOURCE_GAMEPAD && event.getRepeatCount() == 0) {

            DynamicToast.makeWarning(this, "You pressed some button").show();
            if(keyCode == KeyEvent.KEYCODE_BUTTON_START) {
                Log.d("Hello", "menu");
                DynamicToast.makeWarning(this, "You pressed menu").show();
                Intent menuIntent= new Intent(this, MenuActivity.class);
                startActivityForResult(menuIntent, 1);
            }

            if(keyCode == KeyEvent.KEYCODE_BUTTON_A || keyCode == KeyEvent.KEYCODE_BUTTON_B || keyCode == KeyEvent.KEYCODE_BUTTON_X || keyCode == KeyEvent.KEYCODE_BUTTON_Y) {
                net.addTCPPacket(PadUtils.getPacket(state, keyCode));
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 2) {
            this.finishAndRemoveTask();
        }
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

        ViewUtils.setViewColor(rightSlate, (int) (0xFF * axes.get(4)), 0x9F , 0x0, 0x0);


        circleLeft.setX(locationsL[0] + axes.get(0) * 500);
        circleLeft.setY(locationsL[1] - axes.get(1) * 500);

        circleRight.setX((float)locationsR[0]/3 + axes.get(2) * 500);
        circleRight.setY(locationsR[1] - axes.get(3) * 500);

        String packet = gamepad.getAxesPacket(state, axes);

        net.setUDPPacket(packet);

    }
}
