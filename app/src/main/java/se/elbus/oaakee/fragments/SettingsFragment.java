package se.elbus.oaakee.fragments;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import se.elbus.oaakee.R;

/**
 * Fragment to for Settings.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

}
