package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.DropDownPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class MenuFragment extends PreferenceFragmentCompat {
    private String sharedPrefFile = "com.example.myapplication.hellosharedprefs";
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
    }

    SwitchPreferenceCompat barometerPreference;
    EditTextPreference ipAddress;
    SwitchPreferenceCompat trimmingPreference;
    ListPreference flightModePreference;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)  {
        Context context = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);

        barometerPreference = new SwitchPreferenceCompat(context);
        barometerPreference.setKey("barometer");
        barometerPreference.setTitle("Barometer");
        barometerPreference.setSummary("Enable/disable barometer");

        trimmingPreference = new SwitchPreferenceCompat(context);
        trimmingPreference.setKey("trimming");
        trimmingPreference.setTitle("Trimming");
        trimmingPreference.setSummary("Enable/disable trimming");

        flightModePreference = new ListPreference(context);
        flightModePreference.setKey("flightMode");
        flightModePreference.setTitle("Flight Mode");
        flightModePreference.setSummary("Select Flight Mode");
        flightModePreference.setEntries(R.array.flightModes);
        flightModePreference.setEntryValues(R.array.flightModes);
        flightModePreference.setDefaultValue("LEVEL");
        flightModePreference.setDialogTitle(R.string.flight_mode_selector_dialog);
        flightModePreference.setDialogIcon(R.drawable.hawklogo);

        ipAddress = new EditTextPreference(context);
        ipAddress.setKey("ipAddress");
        ipAddress.setTitle("IPv4/IPv6 Address");
        ipAddress.setSummary("192.168.0.20");

        screen.addPreference(ipAddress);
        screen.addPreference(barometerPreference);
        screen.addPreference(trimmingPreference);
        screen.addPreference(flightModePreference);

        setPreferenceScreen(screen);
    }


    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("ipAddress", ipAddress.getText());

        editor.apply();

    }
}
