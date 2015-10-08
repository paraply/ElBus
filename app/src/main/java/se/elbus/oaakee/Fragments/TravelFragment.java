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
    private static ListView mDeparturesListView;
    private VT_Client vtClient;
    private static final String TAG = "Travel";

    private Location location;

    private double latitude = 57.692395;
    private double longitude = 11.972917;

    private List<StopLocation> busStops; //With removed duplicates
    private List<Departure> allDepartures;
    private List<List<Departure>> departuresList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vtClient = new VT_Client(this);

        try {
            Log.i(TAG,"Permission for GPS: " + checkGPSPermission());
            Location location = getCurrentLocation();
            Log.i(TAG,"Latitude: " + location.getLatitude());
            latitude = location.getLatitude();
            longitude = location.getLongitude();

        } catch (SecurityException e){
            Log.e(TAG,"Couldn't get location from gps. Please check GPS permission");
            e.printStackTrace();
        }

        vtClient.get_nearby_stops(latitude + "", longitude + "", "30", "1000");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_travel, container, false);

        allDepartures = new ArrayList<Departure>();

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
        String[] strBusStops = {};

        mBusStopSpinner = (Spinner) v.findViewById(R.id.busStopSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, strBusStops);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mBusStopSpinner.setAdapter(adapter);

        mBusStopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Spinner clicked: " + mBusStopSpinner.getSelectedItem().toString() + ", Position: " + mBusStopSpinner.getSelectedItemPosition());
                //updateDeparturesList(mBusStopSpinner.getSelectedItemPosition());
                int i = mBusStopSpinner.getSelectedItemPosition();
                vtClient.get_departure_board(busStops.get(i).id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG, "Spinner nothing selected");
            }
        });

    }

    private void updateBusStopList(){
        Log.i(TAG, "--- Updating bus stop list ---");

        ArrayList<String> stops = new ArrayList<String>();

        for (StopLocation s : busStops){
            stops.add(s.name);
        }

        //ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, busStops);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,stops);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mBusStopSpinner.setAdapter(adapter1);

    }

    private void sortDeparturesAndUpdateDepartureList(){
        
        updateDeparturesList();

    }

    private void updateDeparturesList(){
        Log.i(TAG, "--- Updating departure list ---");

        //Log.i(TAG, "Stop chosen: " + busStops.get(index).name);


        List<List<Departure>> list = new ArrayList<List<Departure>>();
        list.add(allDepartures);

        ArrayAdapter<List<Departure>> adapter = new DeparturesAdapter(getContext(), list);
        mDeparturesListView.setAdapter(adapter);


    }

    /**
     * Creates a list of allDepartures from an array of strings
     */
    private void createDeparturesList(View v) {

        mDeparturesListView = (ListView) v.findViewById(R.id.departuresListView);

        //String[] allDepartures = {"Lindholmen", "Tynnered", "Bergsjön", "Majorna"};

        List<List<Departure>> list = new ArrayList<List<Departure>>();
        list.add(allDepartures);

        ArrayAdapter<List<Departure>> adapter = new DeparturesAdapter(getContext(), list);
        mDeparturesListView.setAdapter(adapter);

    }


    private void removeDuplicatesAndUpdateStopList(List<StopLocation> stopLocations){
        List<StopLocation> stops = removeDuplicateStops(stopLocations);

        for (StopLocation s : stops) { // List all nearby stops
            Log.i(TAG, "### NEAR STOP" + " ID:" + s.id + " TRACK:" + s.track + " NAME: " + s.name);
        }

        StopLocation closest = stops.get(0); // The closest stop is at the top of the list
        Log.i(TAG, "### CLOSEST STOP " + closest.name + " ID:" + closest.id);

        //vtClient.get_departure_board(closest.id); // Get departures from this stop

        busStops = stops;

        updateBusStopList();
    }


    private List<StopLocation> removeDuplicateStops(List<StopLocation> stopLocations){
        List<StopLocation> stops = new ArrayList<>();

        stops.add(stopLocations.get(0));

        boolean contains;

        for (StopLocation s:stopLocations){
            String stopName = s.name;

            contains = false;

            for (StopLocation d:stops){
                if ((stopName.equals(d.name))) {
                    contains = true;
                }
            }

            if (!contains){
                stops.add(s);
            }
        }

        return stops;
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
        removeDuplicatesAndUpdateStopList(locationList.stoplocation);
    }

    @Override
    public void got_departure_board(DepartureBoard departureBoard) {
        Log.i(TAG,"Departure board ");
        for (Departure d : departureBoard.departure) { // List all the allDepartures from this stop
            Log.i(TAG,"### DEPARTURES - SHORT NAME: " + d.sname + " TIME: " + d.time + " TRACK: " + d.track + " bgColor: " + d.bgColor + " JOURNEYID " + d.journeyid + " DIRECTION: " + d.direction );
            if (d.sname.equals("11") && d.direction.equals("Bergsjön")){ // If spårvagn 11 mot Bergsjön
                vtClient.get_journey_details(d.journeyDetailRef); // Get journey details from this journey
                return;
            }
        }
        allDepartures = departureBoard.departure;

        sortDeparturesAndUpdateDepartureList();

    }

    @Override
    public void got_error(String during_method, String error_msg) {

    }
}
