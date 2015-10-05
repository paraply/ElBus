package se.elbus.oaakee;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class TestActivityFragment extends Fragment {

    public TestActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Intent i = AlarmService.newIntent(getActivity());
        //getActivity().startService(i);
        AlarmService.setServiceAlarm(getActivity(), true);

        return inflater.inflate(R.layout.fragment_test, container, false);
    }
}