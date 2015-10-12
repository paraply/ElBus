package se.elbus.oaakee.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Wrapper for using SettingsFragment (PreferenceFragment) in support library.
 *
 * Created by Tobias on 2015-10-07.
 */
public class SettingsWrapper extends Fragment {
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

}
