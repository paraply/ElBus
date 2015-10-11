package se.elbus.oaakee.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import se.elbus.oaakee.R;

public class TravelFragment extends Fragment {

    private static Spinner mBusStopSpinner;

    private FragmentSwitchCallbacks mFragmentSwitcher;


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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFragmentSwitcher = (FragmentSwitchCallbacks) context;
    }

    /**
     * Custom adapter for departures ListView. <p> TODO: should extend "ArrayAdapter<BusLine>" or
     * something similar instead of "<String>" <p> Created by Tobias on 15-09-27.
     */
    public class DeparturesAdapter extends ArrayAdapter<String> {
        public DeparturesAdapter(Context context, String[] string) {
            super(context, R.layout.busline_row, string);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View customView = layoutInflater.inflate(R.layout.busline_row, parent, false);

            String busDirection = getItem(position);

            final Button topButton = (Button) customView.findViewById(R.id.topDepartureButton);
            final Button bottomButton = (Button) customView.findViewById(R.id.bottomDepartureButton);

            topButton.setText(busDirection);
            bottomButton.setText(busDirection + " 2");

            setButtonAction(topButton);
            setButtonAction(bottomButton);

            return customView;
        }

        private void setButtonAction(final Button button) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String toastMessage = "You clicked: " + button.getText();
                    Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
                    mFragmentSwitcher.nextFragment(null);

                }
            });
        }
    }
}
