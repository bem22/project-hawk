package com.example.myapplication;

import android.os.Bundle;

import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import androidx.appcompat.app.AppCompatActivity;

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
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                == InputDevice.SOURCE_GAMEPAD   ) {
            //net.addTCPPacket(PadUtils.getPacket(state, keyCode));

            DynamicToast.makeWarning(this, "You pressed some button");
            if(keyCode == KeyEvent.KEYCODE_BUTTON_START) {
                DynamicToast.makeSuccess(this, "You pressed menu", 10).show();
                finish();
            }

            if(keyCode == KeyEvent.KEYCODE_BACK) {
                setResult(2, null);
                finish();
                DynamicToast.makeSuccess(this, "Bye!", 500).show();
            }

            if(keyCode == KeyEvent.KEYCODE_BUTTON_A) {
                Log.d("Hello", "A");
                Toast toast = Toast.makeText(this, "A Pressed", Toast.LENGTH_SHORT);
                toast.show();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
