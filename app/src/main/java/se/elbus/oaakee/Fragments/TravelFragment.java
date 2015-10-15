package se.elbus.oaakee.Fragments;

import android.content.Context;
import android.location.Criteria;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import se.elbus.oaakee.R;
import se.elbus.oaakee.REST_API.VT_Callback;
import se.elbus.oaakee.REST_API.VT_Client;
import se.elbus.oaakee.REST_API.VT_Model.Departure;
import se.elbus.oaakee.REST_API.VT_Model.DepartureBoard;
import se.elbus.oaakee.REST_API.VT_Model.JourneyDetail;
import se.elbus.oaakee.REST_API.VT_Model.LocationList;
import se.elbus.oaakee.REST_API.VT_Model.StopLocation;

public class TravelFragment extends Fragment implements VT_Callback, LocationListener {

    private Spinner mBusStops;
    private ArrayAdapter<String> mBusStopsAdapter;

    private ListView mDeparturesList;
    private ArrayAdapter<String> mDepartureListAdapter;

    private VT_Client vtClient;
    private static final String TAG = "Travel";

    private final long LATEST_LOCATION_TIME_MILLIS = 4 * 60 * 1000;
    private final int LOCATION_ACCURACY = Criteria.ACCURACY_LOW; // "For horizontal and vertical position this corresponds roughly to an accuracy of greater than 500 meters."

    double mSimulatorLongitude = 11.970462;
    double mSimulatorLatitude = 57.710259;

    private List<StopLocation> busStops; //With removed duplicates

    private FragmentSwitchCallbacks mFragmentSwitcher;
    private Bundle savedState;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedState = new Bundle();
        vtClient = new VT_Client(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try{
            getLocation(LATEST_LOCATION_TIME_MILLIS, LOCATION_ACCURACY);
        }catch (SecurityException e){
            Log.e(TAG, e.getLocalizedMessage());
        }

        View v = inflater.inflate(R.layout.fragment_travel, container, false); // Main view.

        mBusStops = (Spinner) v.findViewById(R.id.busStopSpinner);

        mBusStopsAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item); // Adapter to put source stops to gui
        mBusStopsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        initBusStopList(mBusStops);
        mDepartureListAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item);
        mDepartureListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mDeparturesList = (ListView) v.findViewById(R.id.departuresListView);

        return v;
    }

    private void initBusStopList(final Spinner spinner) {

        spinner.setAdapter(mBusStopsAdapter); // Connects the adapter to the view
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Spinner clicked: " + spinner.getSelectedItem().toString() + ", Position: " + position);

                StopLocation source = busStops.get(position);

                savedState.putParcelable("source", source);
                vtClient.get_departure_board(source.id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG, "Spinner nothing selected");
            }
        });
    }

    /**
     * This will register this as a listener if it can't find an acceptably old location.
     * It will call the listener method if it found an "old", acceptable location.
     * @param maxLocationAgeMillis is the maximum age of the location in milliseconds.
     * @param locationAccuracy is the acceptable accuracy to have when getting the position.
     * @throws SecurityException
     */
    private void getLocation(long maxLocationAgeMillis, int locationAccuracy) throws SecurityException{
        Criteria locationCriteria = new Criteria();
        locationCriteria.setAccuracy(locationAccuracy);

        LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        String bestProvider = manager.getBestProvider(locationCriteria, false);
        Location fastLocation;
        if(Build.FINGERPRINT.startsWith("generic")){
            fastLocation = new Location(bestProvider);
            fastLocation.setLatitude(mSimulatorLatitude);
            fastLocation.setLongitude(mSimulatorLongitude);
            fastLocation.setTime(System.currentTimeMillis());
        }else{
            fastLocation = manager.getLastKnownLocation(bestProvider);
        }

        /*
         If a long time has passed since the last scan.
         */
        if(fastLocation == null || fastLocation.getTime() + maxLocationAgeMillis < System.currentTimeMillis()){
            if(!manager.isProviderEnabled(bestProvider)){
                warnGpsOff();
            }
            manager.requestSingleUpdate(locationCriteria, this, Looper.myLooper());
        }else {
            onLocationChanged(fastLocation);
        }
    }

    private void warnGpsOff() {
        // TODO: Send warning to user, the GPS is off and the location might be bad.
        Toast.makeText(getContext(), R.string.gps_off_warning_text, Toast.LENGTH_LONG).show();
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
        List<StopLocation> stops1 = new ArrayList<>();
        boolean contains;

        for (StopLocation s: locationList.stoplocation){
            String stopName = s.name;

            contains = false;

            for (StopLocation d: stops1){
                if ((stopName.equals(d.name))) {
                    contains = true;
                }
            }

            if (!contains){
                stops1.add(s);
            }
        }

        List<StopLocation> stops = stops1;

        busStops = stops;

        for (StopLocation s : busStops){
            mDepartureListAdapter.add(s.name.substring(0,s.name.length()-10));
        }
        mBusStops.setAdapter(mDepartureListAdapter);
    }

    @Override
    public void got_departure_board(DepartureBoard board) {
        List<Departure> allDepartures = board.departure;
        List<List<Departure>> departuresSorted = new ArrayList<>();

        List<String> shortName = new ArrayList<>(); //list of unique short name sorted by departure time

        for (Departure departure: allDepartures){
            if (!(shortName.contains(departure.sname))){
                shortName.add(departure.sname);
            }
        }

        for (String s:shortName){
            List<Departure> departures = new ArrayList<>();

            for (Departure departure: allDepartures){ //loop through all departures
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
        ArrayAdapter<List<Departure>> adapter = new DeparturesAdapter(getContext(), departuresSorted);
        mDeparturesList.setAdapter(adapter);
    }

    @Override
    public void got_error(String during_method, String error_msg) {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFragmentSwitcher = (FragmentSwitchCallbacks) context;
    }

    /**
     * Called when the location has changed.
     */
    @Override
    public void onLocationChanged(Location location) {
        // TODO: Change location in GUI
        vtClient.get_nearby_stops(location.getLatitude() + "", location.getLongitude() + "", "30", "1000");

    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    @Override
    public void onProviderEnabled(String provider) {

    }
    @Override
    public void onProviderDisabled(String provider) {

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
                Drawable circle = lineNumber.getBackground();
                try {
                    circle.setColorFilter(Color.parseColor(((departures.get(0)).fgColor)), PorterDuff.Mode.MULTIPLY);
                } catch (IllegalArgumentException e){
                    Log.e(TAG, "Failed parsing color, set to a default colour");
                    circle.setColorFilter(Color.parseColor("#CCCCCC"), PorterDuff.Mode.MULTIPLY);
                }
            }

            for (Departure departure:departures){
                String time;
                String date;

                if (departure.rtTime!=null){
                    time = departure.rtTime;
                    date = departure.rtDate;
                } else {
                    time = departure.time;
                    date = departure.date;
                }

                String minutesToDeparture;
                try {
                    long minutes = minutesToDeparture(date, time);
                    minutesToDeparture = ""+minutes;
                } catch (NumberFormatException e){
                    e.printStackTrace();
                    minutesToDeparture = "x"; //shows "x" if something goes wrong...
                }

                View busLineButtonView = createBusLineButton(customView, layoutInflater, parent, departure.direction, minutesToDeparture);
                setButtonClick(busLineButtonView,departure);
            }

            return customView;
        }

        /**
         * Calculates minutes to departure from current time
         *
         * @param date Date in format "YYYY-MM-DD"
         * @param time Time in format "HH-MM"
         * @return difference from current time in minutes
         * @throws NumberFormatException If date or time is in wrong format.
         */
        private long minutesToDeparture(String date, String time) throws NumberFormatException{
            int year = Integer.valueOf(date.substring(0, 4));
            int month = Integer.valueOf(date.substring(5, 7));
            int day = Integer.valueOf(date.substring(8, 10));
            int hour = Integer.valueOf(time.substring(0, 2));
            int minute = Integer.valueOf(time.substring(3, 5));

            Calendar temp = Calendar.getInstance();
            temp.set(year,month-1,day,hour,minute); //departure time. Month starts at 0

            Calendar today = Calendar.getInstance();

            long difference = temp.getTime().getTime() - today.getTime().getTime();
            difference /= 1000*60; //time in minutes

            return difference;
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
                    savedState.putParcelable("trip", departure);
                    mFragmentSwitcher.nextFragment(savedState);
                    Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
