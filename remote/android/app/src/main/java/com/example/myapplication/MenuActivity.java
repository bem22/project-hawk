package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.widget.Toast;

import static java.lang.Thread.sleep;

public class MenuActivity extends AppCompatActivity {
    private int mCount = 0;

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
        SharedPreferences sharedPref = getSharedPreferences("MyData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("count", mCount);
        editor.apply();

        Toast.makeText(this, "Your details has been save", Toast.LENGTH_SHORT).show();
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
                mCount = 100;
                Toast toast = Toast.makeText(this, "A Pressed", Toast.LENGTH_SHORT);
                toast.show();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
