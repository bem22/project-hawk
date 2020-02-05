package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

public class MenuFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);

        SwitchPreferenceCompat notificationPreference = new SwitchPreferenceCompat(context);
        notificationPreference.setKey("notifications");
        notificationPreference.setTitle("Enable message notifications");

        Preference feedbackPreference = new Preference(context);
        feedbackPreference.setKey("feedback");
        feedbackPreference.setTitle("Send feedback");
        feedbackPreference.setSummary("Report technical issues or suggest new features");

        screen.addPreference(notificationPreference);
        screen.addPreference(feedbackPreference);

        setPreferenceScreen(screen);
    }
}
