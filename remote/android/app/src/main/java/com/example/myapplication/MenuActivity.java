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
    private int mCount = 0;


    private String sharedPrefFile = "com.example.myapplication.hellosharedprefs";
    private SharedPreferences sharedPreferences;
    SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_screen);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, new MenuFragment()).commit();

    }

    @Override
    protected void onPause(){
        super.onPause();
        sharedPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("count", mCount);
        editor.apply();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                == InputDevice.SOURCE_GAMEPAD   ) {
            //net.addTCPPacket(PadUtils.getPacket(state, keyCode));

            DynamicToast.makeWarning(this, "You pressed some button");
            if(keyCode == KeyEvent.KEYCODE_BUTTON_START) {
                DynamicToast.makeSuccess(this, "Back to controller. Settings not saved", 10).show();
                finish();
            }

            if(keyCode == KeyEvent.KEYCODE_BACK) {
                setResult(2, null);
                finish();
            }

            if(keyCode == KeyEvent.KEYCODE_BUTTON_A) {
                Log.d("Hello", "A");
                mCount = 100;
                Toast toast = Toast.makeText(this, "A Pressed", Toast.LENGTH_SHORT);
                toast.show();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
