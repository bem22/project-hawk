package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class MenuFragment extends PreferenceFragmentCompat {
    private String sharedPrefFile = "com.example.myapplication.hellosharedprefs";
    private SharedPreferences sharedPreferences;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)  {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
