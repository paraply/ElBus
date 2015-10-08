package se.elbus.oaakee.Fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

import se.elbus.oaakee.R;
import se.elbus.oaakee.REST_API.VT_Callback;
import se.elbus.oaakee.REST_API.VT_Client;
import se.elbus.oaakee.REST_API.VT_Model.Departure;
import se.elbus.oaakee.REST_API.VT_Model.DepartureBoard;
import se.elbus.oaakee.REST_API.VT_Model.JourneyDetail;
import se.elbus.oaakee.REST_API.VT_Model.LocationList;
import se.elbus.oaakee.REST_API.VT_Model.StopLocation;

public class TravelFragment extends Fragment implements VT_Callback {

    private static Spinner mBusStopSpinner;
    private VT_Client vtClient;
    private static final String TAG = "Travel";

    private Location location;

    private double latitude = 57.692395;
    private double longitude = 11.972917;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vtClient = new VT_Client(this);
        vtClient.get_nearby_stops(latitude + "", longitude + "", "30", "1000");
        Log.i(TAG, "Get nearby stops");

        try {
            Log.i(TAG,"Permission for GPS: " + checkGPSPermission());
            Location location = getCurrentLocation();
            Log.i(TAG,"Latitude: " + location.getLatitude());

        } catch (SecurityException e){
            Log.e(TAG,"Couldn't get location from gps. Please check GPS permission");
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_travel, container, false);

        createBusStopList(v);
        createDeparturesList(v);

        return v;
    }


    private Location getCurrentLocation() throws SecurityException{
        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return location;
    }

    private boolean checkGPSPermission(){
        int permission = getContext().checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
        return permission == PackageManager.PERMISSION_GRANTED;
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

        mBusStopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Spinner clicked: " + mBusStopSpinner.getSelectedItem().toString());
                updateDeparturesList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG, "Spinner nothing selected");
            }
        });

    }

    private void updateBusStopList(List<StopLocation> stopLocations){

        ArrayList<String> stops = new ArrayList<String>();

        for (StopLocation s : stopLocations){
            stops.add(s.name);
        }

        //ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, busStops);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,stops);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mBusStopSpinner.setAdapter(adapter1);



    }

    private void updateDeparturesList(){
        Log.i(TAG,"Updated departure list");

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
    public void got_journey_details(JourneyDetail journeyDetail) {

    }

    @Override
    public void got_nearby_stops(LocationList locationList) {
        for (StopLocation s : locationList.stoplocation) { // List all nearby stops
            Log.i(TAG, "### NEAR STOP " + s.name + " ID:" + s.id + " TRACK:" + s.track);
        }
        StopLocation closest = locationList.stoplocation.get(0); // The closest stop is at the top of the list
        Log.i(TAG, "### CLOSEST STOP " + closest.name + " ID:" + closest.id + " TRACK:" +  closest.track );
        vtClient.get_departure_board(closest.id); // Get departures from this stop

        updateBusStopList(locationList.stoplocation);
    }

    @Override
    public void got_departure_board(DepartureBoard departureBoard) {
        Log.i(TAG,"Departure board");
        for (Departure d : departureBoard.departure) { // List all the departures from this stop
            Log.i(TAG,"### DEPARTURES: " + d.name  + " SHORT NAME: " + d.sname + " DIRECTION: " + d.direction);
            if (d.sname.equals("11") && d.direction.equals("Bergsjön")){ // If spårvagn 11 mot Bergsjön
                vtClient.get_journey_details(d.journeyDetailRef); // Get journey details from this journey
                return;
            }
        }

    }

    @Override
    public void got_error(String during_method, String error_msg) {

    }
}
