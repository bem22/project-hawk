package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;

import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.widget.Toast;

import static java.lang.Thread.sleep;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_screen);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, new MenuFragment()).commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                == InputDevice.SOURCE_GAMEPAD   ) {

            DynamicToast.makeWarning(this, "You pressed some button");
            if(keyCode == KeyEvent.KEYCODE_BUTTON_START) {
                DynamicToast.makeSuccess(this, "Back to controller. Settings not saved", 10).show();
                finish();
            }

            if(keyCode == KeyEvent.KEYCODE_BACK) {
                setResult(2, null);
                finish();
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}
