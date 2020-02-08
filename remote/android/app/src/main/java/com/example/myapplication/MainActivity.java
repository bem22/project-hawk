package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;
import java.util.Arrays;

import utils.PadUtils;
import utils.ViewUtils;

public class MainActivity extends Activity{

    private SharedPreferences.OnSharedPreferenceChangeListener preferencesListener;

    SharedPreferences prefs;

    //TODO: Move to ViewUtils
    CircleView circleLeft;
    CircleView circleRight;

    RelativeLayout rightSlate;

    RemoteState state = new RemoteState();
    PadUtils gamepad = new PadUtils();
    ViewUtils views;
    NetworkManager net;

    // TODO: Move to PadUtils
    ArrayList<Float> axes = new ArrayList<>(Arrays.asList((float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0));

    // TODO: Refactor locationsL/R and move to View utils
    int[] locationsL = new int[2];
    int[] locationsR = new int[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences (this);

        // Invoke Network Manager with ipAddress from preferences
        net = new NetworkManager(prefs.getString("ipAddress",""));

        // Set up the preference listener
        preferencesListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals("ipAddress")) {
                    net.setIpAddress(sharedPreferences.getString(key, ""));
                    state.setTrimmer(sharedPreferences.getBoolean("trimmer", true));
                }
            }

        };

        setContentView(R.layout.activity_main);

        views = new ViewUtils(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        circleLeft = findViewById(R.id.circle_left);
        circleRight = findViewById(R.id.circle_right);
        rightSlate = findViewById(R.id.rightSlate);

        Button connectButton = findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(!state.getConnectionStatus()) {
                    state.setConnectionStatus(true);
                    net.startConnection();
                } else {
                    net.closeConnections();
                    net.startConnection();
                    //TODO: Return from network and set visibility state
                    //TODO: When network fails, show button again
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        prefs.registerOnSharedPreferenceChangeListener(preferencesListener);
    }

    // TODO: What is this?
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
                DynamicToast.makeWarning(this, "Welcome to the menu").show();
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


    // TODO: This is no longer needed
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 2) {

            DynamicToast.makeSuccess(this, "Settings saved successfully!" + prefs.getString("ipAddress", " ")).show();
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

    // Move to padUtils?


    // TODO: Move to padUtils and add callback for NetworkManager/ViewUtils/
    private void processJoystickInput(MotionEvent event,
                                      int historyPos) {
        InputDevice inputDevice = event.getDevice();

        // Calculate the horizontal distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat axis, or the right control stick.
        axes.set(0, PadUtils.getCenteredAxis(event, inputDevice, MotionEvent.AXIS_X, historyPos));
        axes.set(1, -PadUtils.getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Y, historyPos));
        axes.set(2, PadUtils.getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Z, historyPos));
        axes.set(3, -PadUtils.getCenteredAxis(event, inputDevice, MotionEvent.AXIS_RZ, historyPos));
        axes.set(4, event.getAxisValue(MotionEvent.AXIS_GAS));
        axes.set(5, event.getAxisValue(MotionEvent.AXIS_BRAKE));

        Float gainControllValue = PadUtils.getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_X, historyPos);
        if(gainControllValue != 0) {
            if(gainControllValue > 0) {
                state.increaseGain();

            } else if(gainControllValue < 0) {
                state.decreaseGain();
            }

            DynamicToast.makeWarning(this, " " + state.getGain()).show();
        }

        // For some reason DPAD DOWN and DPAD up are inverted (DPAD DOWN = 1, DPAD UP = -1)
        Float throttleControllValue = PadUtils.getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_Y, historyPos);
        if(throttleControllValue != 0) {
            if(throttleControllValue > 0) {
                state.decreaseThrottle();

            } else if(throttleControllValue < 0) {
                state.increaseThrottle();
            }

            DynamicToast.makeSuccess(this, " " + state.getThrottle()).show();
        }

        axes.set(1, -PadUtils.getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_Y, historyPos));

        axes.set(2, PadUtils.getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_X, historyPos));

        axes.set(3, -PadUtils.getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_Y, historyPos));


        ViewUtils.setViewColor(rightSlate, (int) (0xFF * axes.get(4)), 0x9F , 0x0, 0x0);


        circleLeft.setX(locationsL[0] + axes.get(0) * 500);
        circleLeft.setY(locationsL[1] - axes.get(1) * 500);

        circleRight.setX((float)locationsR[0]/3 + axes.get(2) * 500);
        circleRight.setY(locationsR[1] - axes.get(3) * 500);

        //TODO: Add ControllerMode function here

        String packet = gamepad.getAxesPacket(state, axes);

        net.setUDPPayload(packet);
    }

}
