package se.elbus.oaakee.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import se.elbus.oaakee.R;

public class TravelFragment extends Fragment {

    private static Spinner mBusStopSpinner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_travel, container, false);

        createBusStopList(v);
        createDeparturesList(v);

        return v;
    }


    /**
     * Populates the bus stop spinner
     */
    private void createBusStopList(View v) {
        String[] busStops = {"Chalmers", "Kapellplatsen", "Vasaplatsen", "Valand",
                "Kungsportsplatsen", "Brunnsparken", "Centralstationen", "Långanamnhållplatsen abcdefgh"};

        mBusStopSpinner = (Spinner) v.findViewById(R.id.busStopSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, busStops);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mBusStopSpinner.setAdapter(adapter);
    }

    /**
     * Creates a list of departures from an array of strings
     */
    private void createDeparturesList(View v) {
        String[] departures = {"Lindholmen", "Tynnered", "Bergsjön", "Majorna"};

        ArrayAdapter<String> adapter = new DeparturesAdapter(getContext(), departures);
        ListView departuresListView = (ListView) v.findViewById(R.id.departuresListView);
        departuresListView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}