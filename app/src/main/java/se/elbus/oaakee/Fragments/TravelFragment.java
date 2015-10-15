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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

    private double latitude = 57.692395;
    private double longitude = 11.972917;

    private List<StopLocation> busStops; //With removed duplicates
    private List<Departure> allDepartures;
    private List<List<Departure>> departuresSorted;

    private StopLocation chosenStopLocation;

    private FragmentSwitchCallbacks mFragmentSwitcher;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        allDepartures = new ArrayList<>();
        departuresSorted = new ArrayList<>();

        vtClient = new VT_Client(this);
        
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try {
            Log.i(TAG,"Permission for GPS: " + checkGPSPermission());
            Location location = getCurrentLocation();
            Log.i(TAG,"Latitude: " + location.getLatitude());
            latitude = location.getLatitude();
            longitude = location.getLongitude();

        } catch (Exception e){
            Log.e(TAG,"Couldn't get location from gps. Please check GPS permission");
            e.printStackTrace();
        }

        vtClient.get_nearby_stops(latitude + "", longitude + "", "30", "1000");


        View v = inflater.inflate(R.layout.fragment_travel, container, false);

        createBusStopList(v);
        createDeparturesList(v);

        return v;
    }


    private Location getCurrentLocation() throws SecurityException{
        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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
                int i = mBusStopSpinner.getSelectedItemPosition();
                chosenStopLocation = busStops.get(i);
                vtClient.get_departure_board(chosenStopLocation.id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG, "Spinner nothing selected");
            }
        });

    }

    /**
     * Updates bus stop list (gui)
     */
    private void updateBusStopList(){
        ArrayList<String> stops = new ArrayList<>();

        for (StopLocation s : busStops){
            stops.add(s.name);
        }

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, stops);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mBusStopSpinner.setAdapter(adapter1);
    }

    /**
     * Sorts departures list, then updates gui
     */
    private void sortDeparturesAndUpdateDepartureList(){
        sortDepartures();
        updateDeparturesList();
    }

    /**
     * Sorts departures, puts them in listes of bus line number
     */
    private void sortDepartures(){
        departuresSorted.clear();

        List<String> shortName = new ArrayList<>(); //list of unique short name sorted by departure time

        for (Departure departure:allDepartures){
            if (!(shortName.contains(departure.sname))){
                shortName.add(departure.sname);
            }
        }

        for (String s:shortName){ //go through all lines
            //Log.i(TAG,"Short: " + s);
            List<Departure> departures = new ArrayList<>();

            for (Departure departure:allDepartures){ //loop through all departures
                if (departure.sname.equals(s)){
                    boolean alreadyInList = false;

                    for (Departure d2:departures){ //Checks if direction of this departure is already in list
                        if (departure.direction.equals(d2.direction)){
                            alreadyInList=true;
                            break;
                        }
                    }

                    if (!alreadyInList){
                        departures.add(departure);
                        //Log.d(TAG,"Added: " + departure.sname + ", Dir: " + departure.direction + ", time: " + departure.time);
                    }
                }
            }
            departuresSorted.add(departures); //Adds list of departures for one single bus line number
        }
    }

    /**
     * Updates departures list, giving it the sorted departures list
     */
    private void updateDeparturesList(){
        ArrayAdapter<List<Departure>> adapter = new DeparturesAdapter(getContext(), departuresSorted);
        mDeparturesListView.setAdapter(adapter);
    }

    /**
     * Creates a list of allDepartures from an array of strings
     */
    private void createDeparturesList(View v) {

        mDeparturesListView = (ListView) v.findViewById(R.id.departuresListView);

        //String[] allDepartures = {"Lindholmen", "Tynnered", "Bergsjön", "Majorna"};

        List<List<Departure>> list = new ArrayList<>();
        list.add(allDepartures);

        ArrayAdapter<List<Departure>> adapter = new DeparturesAdapter(getContext(), list);
        mDeparturesListView.setAdapter(adapter);

    }

    /**
     * Removes duplicate bus stops (tracks!) and updates gui.
     * @param stopLocations List of bus stops
     */
    private void removeDuplicatesAndUpdateStopList(List<StopLocation> stopLocations){
        List<StopLocation> stops = removeDuplicateStops(stopLocations);

/*        for (StopLocation s : stops) { // List all nearby stops
            Log.i(TAG, "### NEAR STOP" + " ID:" + s.id + " TRACK:" + s.track + " NAME: " + s.name);
        }

        StopLocation closest = stops.get(0); // The closest stop is at the top of the list
        Log.i(TAG, "### CLOSEST STOP " + closest.name + " ID:" + closest.id);*/

        busStops = stops;
        updateBusStopList();
    }

    /**
     * Removes duplicate bus stops (tracks)
     * @param stopLocations Unsorted list of busstops
     * @return List of bus stops without duplicates
     */
    private List<StopLocation> removeDuplicateStops(List<StopLocation> stopLocations){
        List<StopLocation> stops = new ArrayList<>();

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
       /* Log.i(TAG,"Departure board ");
        for (Departure d : departureBoard.departure) { // List all the allDepartures from this stop
            Log.i(TAG,"### DEPARTURES - SHORT NAME: " + d.sname + " TIME: " + d.time + " TRACK: " + d.track + " bgColor: " + d.bgColor + " JOURNEYID " + d.journeyid + " DIRECTION: " + d.direction );
            if (d.sname.equals("11") && d.direction.equals("Bergsjön")){ // If spårvagn 11 mot Bergsjön
                vtClient.get_journey_details(d.journeyDetailRef); // Get journey details from this journey
                return;
            }
        }*/
        allDepartures = departureBoard.departure;
        sortDeparturesAndUpdateDepartureList();
    }

    @Override
    public void got_error(String during_method, String error_msg) {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFragmentSwitcher = (FragmentSwitchCallbacks) context;
    }
    /**
     * Custom adapter for departures ListView.
     *
     * Created by Tobias on 15-09-27.
     */
    public class DeparturesAdapter extends ArrayAdapter<List<Departure>> {
        public DeparturesAdapter(Context context, List<List<Departure>> departures) {
            super(context, R.layout.busline_row, departures);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View customView = layoutInflater.inflate(R.layout.busline_row, parent, false);

            List<Departure> departures = getItem(position);

            TextView lineNumber = (TextView)customView.findViewById(R.id.busNumberTextView);

            if (departures.size()>0) {
                lineNumber.setText(departures.get(0).sname);
            }

            for (Departure departure:departures){

                View busLineButtonView = createBusLineButton(customView, layoutInflater, parent, departure.direction, departure.time);
                setButtonClick(busLineButtonView,departure);

            }

            return customView;
        }

        /**
         * Created button for bus line departure
         * @param parentView
         * @param layoutInflater
         * @param parent
         * @param direction
         * @param time
         * @return
         */
        private View createBusLineButton(View parentView, LayoutInflater layoutInflater, ViewGroup parent, String direction, String time){
            View busLineButtonView = layoutInflater.inflate(R.layout.busline_button,parent,false);

            LinearLayout linearLayout = (LinearLayout)parentView.findViewById(R.id.busLineButtonLayout);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            linearLayout.addView(busLineButtonView, layoutParams);

            setTextViewText(R.id.stationTextView,busLineButtonView,direction);
            setTextViewText(R.id.minutesTextView,busLineButtonView,time);

            return busLineButtonView;
        }

        /**
         * Sets text of TextView
         * @param id
         * @param parent
         * @param text
         */
        private void setTextViewText(int id, View parent, String text){
            TextView textView = (TextView)parent.findViewById(id);
            textView.setText(text);
        }

        /**
         * Sets button click
         * @param view
         */
        private void setButtonClick(final View view, final Departure departure){
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String toastMessage = "You clicked: " + ((TextView)view.findViewById(R.id.stationTextView)).getText();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("source", chosenStopLocation);
                    bundle.putParcelable("trip",departure);
                    mFragmentSwitcher.nextFragment(bundle);
                    Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
