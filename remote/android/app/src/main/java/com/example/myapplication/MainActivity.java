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
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.pranavpandey.android.dynamic.toasts.DynamicToast;

public class MainActivity extends Activity{

    private SharedPreferences.OnSharedPreferenceChangeListener preferencesListener;
    SharedPreferences prefs;

    RemoteState state = new RemoteState();
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
                }

                if(key.equals("trimmer")) {
                    state.setTrimmer(sharedPreferences.getBoolean("trimmer", true));
                }

                if(key.equals("flightMode")) {
                    state.setFlightMode(sharedPreferences.getString("flightMode", "ANGLE"));
                }

                if(key.equals("barometer")) {
                    state.setBarometerStatus((sharedPreferences.getBoolean("barometer", false)));
                }

                if(key.equals("magnetometer")) {
                    state.setMagnetometerStatus((sharedPreferences.getBoolean("magnetometer", false)));
                }

                net.setUDPPayload();
            }
        };

        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        views.stickPositionLeft = findViewById(R.id.circle_left);
        views.stickPositionRight= findViewById(R.id.circle_right);
        views.connectionIcon = findViewById(R.id.connection_status);
        views.batteryPercentageText = findViewById(R.id.battery_level_text);
        views.batteryPercentageIcon = findViewById(R.id.battery_level_icon);

        views.connectButton = findViewById(R.id.connectButton);
        views.connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(!state.getConnectionStatus()) {
                    net.startConnection();
                } else {
                    net.closeConnections();
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

            if(keyCode == KeyEvent.KEYCODE_BUTTON_R1 || keyCode == KeyEvent.KEYCODE_BUTTON_L1) {
            }

            if(keyCode == KeyEvent.KEYCODE_BUTTON_MODE) {
                DynamicToast.makeSuccess(this, "Attempting to connect...").show();
                views.connectButton.performClick();
            }

            if(keyCode == KeyEvent.KEYCODE_BUTTON_THUMBR || keyCode == KeyEvent.KEYCODE_BUTTON_THUMBL || keyCode == KeyEvent.KEYCODE_BUTTON_SELECT) {
                state.setButtonState(keyCode);
            }


            Log.d("hello", keyCode + "hello from keys");
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                == InputDevice.SOURCE_GAMEPAD && event.getRepeatCount() == 0) {
            state.resetButtonState(keyCode);
        }

        return super.onKeyUp(keyCode, event);
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

    Toast tot;

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
        state.getRawAxes().set(4, event.getAxisValue(MotionEvent.AXIS_BRAKE));
        Float armControlValue = PadUtils.getCenteredAxis(event, inputDevice, MotionEvent.AXIS_RZ, historyPos);

        if(armControlValue > .9f && state.getPilotArmingStatus() && state.getSecurityArmButtonState()) {
            state.disarmDrone();
        } else if(armControlValue < -.9f && !state.getPilotArmingStatus() && state.getSecurityArmButtonState()) {
            state.armDrone();
        }

        Float gainControlValue = PadUtils.getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_X, historyPos);
        if(gainControlValue != 0) {
            if(gainControlValue > 0) {
                state.increaseGain();

            } else if(gainControlValue < 0) {
                state.decreaseGain();
            }
            if(tot != null) {
                tot.cancel();
            }
            tot = Toast.makeText(this, state.getGain() + " ", Toast.LENGTH_SHORT);
            tot.show();

        }

        // For some reason DPAD DOWN and DPAD up are inverted (DPAD DOWN = 1, DPAD UP = -1)
        Float throttlePlatform = PadUtils.getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_Y, historyPos);
        if(throttlePlatform != 0) {
            if(throttlePlatform > 0) {
                state.decreaseThrottle();

            } else if(throttlePlatform < 0) {
                state.increaseThrottle();
            }

            if(tot != null) {
                tot.cancel();
            }
            tot = Toast.makeText(this, state.getThrottlePlatform() + " ", Toast.LENGTH_SHORT);
            tot.show();
        }

        views.updateAxesUI();

        net.setUDPPayload();
    }

}
