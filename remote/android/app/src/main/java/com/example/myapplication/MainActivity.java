package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.preference.PreferenceManager;

import com.pranavpandey.android.dynamic.toasts.DynamicToast;

public class MainActivity extends Activity{

    private SharedPreferences.OnSharedPreferenceChangeListener preferencesListener;
    SharedPreferences prefs;

    RemoteState state = new RemoteState();
    PadUtils gamepad = new PadUtils();
    ViewUtils views;
    NetworkManager net;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        views = new ViewUtils(state);
        prefs = PreferenceManager.getDefaultSharedPreferences (this);

        // Invoke Network Manager with ipAddress from preferences
        net = new NetworkManager(prefs.getString("ipAddress",""), views, state);

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

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        views.stickPositionLeft = findViewById(R.id.circle_left);
        views.stickPositionRight= findViewById(R.id.circle_right);
        views.connectionIcon = findViewById(R.id.connection_status);

        views.connectButton = findViewById(R.id.connectButton);
        views.connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(!state.getConnectionStatus()) {
                    net.startConnection();
                } else {
                    net.closeConnections();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                == InputDevice.SOURCE_GAMEPAD && event.getRepeatCount() == 0) {

            if(keyCode == KeyEvent.KEYCODE_BUTTON_START) {
                DynamicToast.makeWarning(this, "Welcome to the menu").show();
                Intent menuIntent= new Intent(this, MenuActivity.class);
                startActivityForResult(menuIntent, 1);
            }

            if(keyCode == KeyEvent.KEYCODE_BUTTON_A || keyCode == KeyEvent.KEYCODE_BUTTON_B || keyCode == KeyEvent.KEYCODE_BUTTON_X || keyCode == KeyEvent.KEYCODE_BUTTON_Y) {
                net.addTCPPacket(keyCode);
            }

            if(keyCode == KeyEvent.KEYCODE_BUTTON_MODE) {
                DynamicToast.makeSuccess(this, "Attempting to connect...").show();
                views.connectButton.performClick();
                //TODO Add connect functionality here
            }

            Log.d(keyCode+ "" , "hello");
            return true;
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

    // TODO: Move to padUtils and add callback for NetworkManager/ViewUtils/
    private void processJoystickInput(MotionEvent event,
                                      int historyPos) {
        InputDevice inputDevice = event.getDevice();

        // Calculate the horizontal distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat axis, or the right control stick.

        state.getRawAxes().set(0, event.getAxisValue(MotionEvent.AXIS_GAS));
        state.getRawAxes().set(1, PadUtils.getCenteredAxis(event, inputDevice, MotionEvent.AXIS_X, historyPos));
        state.getRawAxes().set(2, -PadUtils.getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Y, historyPos));
        state.getRawAxes().set(3, PadUtils.getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Z, historyPos));
        state.getRawAxes().set(4, -PadUtils.getCenteredAxis(event, inputDevice, MotionEvent.AXIS_RZ, historyPos));

        Float gainControlValue = PadUtils.getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_X, historyPos);
        if(gainControlValue != 0) {
            if(gainControlValue > 0) {
                state.increaseGain();

            } else if(gainControlValue < 0) {
                state.decreaseGain();
            }
            DynamicToast.makeWarning(this, " " + state.getGain()).show();
        }

        // For some reason DPAD DOWN and DPAD up are inverted (DPAD DOWN = 1, DPAD UP = -1)
        Float throttleControlValue = PadUtils.getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_Y, historyPos);
        if(throttleControlValue != 0) {
            if(throttleControlValue > 0) {
                state.decreaseThrottle();

            } else if(throttleControlValue < 0) {
                state.increaseThrottle();
            }
            DynamicToast.makeSuccess(this, " " + state.getThrottle()).show();
        }



        views.updateAxesUI();


        //TODO: Add ControllerMode function here

        String packet = net.getAxesPacket(state);

        net.setUDPPayload(packet);
    }

}
