package se.elbus.oaakee.Fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import se.elbus.oaakee.R;

/**
 * Fragment to for Settings.
 *
 * Created by Tobias on 2015-10-02.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
