package se.elbus.oaakee.fragments;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import se.elbus.oaakee.R;
import se.elbus.oaakee.restapi.VTCallback;
import se.elbus.oaakee.restapi.VTClient;
import se.elbus.oaakee.restapi.vtmodel.Departure;
import se.elbus.oaakee.restapi.vtmodel.DepartureBoard;
import se.elbus.oaakee.restapi.vtmodel.JourneyDetail;
import se.elbus.oaakee.restapi.vtmodel.LocationList;
import se.elbus.oaakee.restapi.vtmodel.StopLocation;

public class TravelFragment extends Fragment implements VTCallback, LocationListener, AdapterView.OnItemSelectedListener, ITravelView {

    protected static final String TAG = "Travel";
    private final long LATEST_LOCATION_TIME_MILLIS = 1 * 60 * 1000;
    private final int LOCATION_ACCURACY = Criteria.ACCURACY_LOW; // "For horizontal and vertical position this corresponds roughly to an accuracy of greater than 500 meters."

    double mSimulatorLongitude = 11.972305;
    double mSimulatorLatitude = 57.707792;

    private Spinner mBusStops;
    private ListView mDeparturesList;

    private ArrayAdapter<String> mDepartureListAdapter;
    private ArrayAdapter<List<Departure>> mDeparturesAdapter;

    private final List<List<Departure>> mDeparturesSorted = new ArrayList<>();
    private final List<StopLocation> mBusStopList = new ArrayList<>(); //With removed duplicates

    private VTClient mVTClient;
    private FragmentSwitchCallbacks mFragmentSwitcher;
    private Bundle mSavedState;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFragmentSwitcher = (FragmentSwitchCallbacks) context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSavedState = new Bundle();
        mVTClient = new VTClient(this);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_travel, container, false); // Main view.

        mDepartureListAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item);
        mDepartureListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBusStops = (Spinner) v.findViewById(R.id.busStopSpinner);
        mBusStops.setOnItemSelectedListener(this);
        mBusStops.setAdapter(mDepartureListAdapter);

        mDeparturesAdapter = new DeparturesAdapter(getContext(), mDeparturesSorted, this);
        mDeparturesList = (ListView) v.findViewById(R.id.departuresListView);
        mDeparturesList.setAdapter(mDeparturesAdapter);

        return v;
    }
    @Override
    public void onStart() {
        super.onStart();

        try {
            getLocation(LATEST_LOCATION_TIME_MILLIS, LOCATION_ACCURACY);
        } catch (SecurityException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    @Override
    public void handleJourneyDetails(JourneyDetail journeyDetail) {

    }
    @Override
    public void handleNearbyStops(LocationList locationList) {
        List<StopLocation> stops1 = new ArrayList<>();
        boolean contains;

        for (StopLocation s : locationList.stoplocation) {
            String stopName = s.name;

            contains = false;

            for (StopLocation d : stops1) {
                if ((stopName.equals(d.name))) {
                    contains = true;
                }
            }

            if (!contains) {
                stops1.add(s);
            }
        }

        List<StopLocation> stops = stops1;

        mBusStopList.clear();
        mBusStopList.addAll(stops);

        mDepartureListAdapter.clear();
        for (StopLocation s : mBusStopList) {
            mDepartureListAdapter.add(s.getNameWithoutCity());
        }
        mDepartureListAdapter.notifyDataSetChanged();
    }
    @Override
    public void handleDepartureBoard(DepartureBoard board) {
        List<Departure> allDepartures = board.departure;
        mDeparturesSorted.clear();

        List<String> shortName = new ArrayList<>(); //list of unique short name sorted by departure time

        for (Departure departure : allDepartures) {
            if (!(shortName.contains(departure.sname))) {
                shortName.add(departure.sname);
            }
        }

        for (String s : shortName) {
            List<Departure> departures = new ArrayList<>();

            for (Departure departure : allDepartures) { //loop through all departures
                if (departure.sname.equals(s)) {
                    boolean alreadyInList = false;

                    for (Departure d2 : departures) { //Checks if direction of this departure is already in list
                        if (departure.direction.equals(d2.direction)) {
                            alreadyInList = true;
                            break;
                        }
                    }

                    if (!alreadyInList) {
                        departures.add(departure);
                        //Log.d(TAG,"Added: " + departure.sname + ", Dir: " + departure.direction + ", time: " + departure.time);
                    }
                }
            }
            mDeparturesSorted.add(departures); //Adds list of departures for one single bus line number
        }

        mDeparturesAdapter.notifyDataSetChanged();
    }
    @Override
    public void handleError(String during_method, String error_msg) {
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "Spinner clicked: " + mBusStops.getSelectedItem().toString() + ", Position: " + position);

        StopLocation source = mBusStopList.get(position);

        mSavedState.putParcelable("source", source);
        mVTClient.getDepartureBoard(source.id);
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.i(TAG, "Spinner nothing selected");
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO: Change location in GUI
        mVTClient.getNearbyStops(location.getLatitude() + "", location.getLongitude() + "", "30", "1000");

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
     * This will register this as a listener if it can't find an acceptably old location. It will
     * call the listener method if it found an "old", acceptable location.
     *
     * @param maxLocationAgeMillis is the maximum age of the location in milliseconds.
     * @param locationAccuracy     is the acceptable accuracy to have when getting the position.
     */
    private void getLocation(long maxLocationAgeMillis, int locationAccuracy) throws SecurityException {
        Criteria locationCriteria = new Criteria();
        locationCriteria.setAccuracy(locationAccuracy);

        LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        String bestProvider = manager.getBestProvider(locationCriteria, false);
        Location fastLocation;
        if (Build.FINGERPRINT.startsWith("generic")) {
            fastLocation = new Location(bestProvider);
            fastLocation.setLatitude(mSimulatorLatitude);
            fastLocation.setLongitude(mSimulatorLongitude);
            fastLocation.setTime(System.currentTimeMillis());
        } else {
            fastLocation = manager.getLastKnownLocation(bestProvider);
        }

        /*
         If a long time has passed since the last scan.
         */
        if (fastLocation == null || fastLocation.getTime() + maxLocationAgeMillis < System.currentTimeMillis()) {
            if (!manager.isProviderEnabled(bestProvider)) {
                warnGpsOff();
            }
            manager.requestSingleUpdate(locationCriteria, this, Looper.myLooper());
        } else {
            onLocationChanged(fastLocation);
        }
    }
    private void warnGpsOff() {
        // TODO: Send warning to user, the GPS is off and the location might be bad.
        Toast.makeText(getContext(), R.string.gps_off_warning_text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void saveParcelable(String key, Parcelable parcelable) {
        mSavedState.putParcelable(key,parcelable);
    }

    @Override
    public void nextFragment() {
        mFragmentSwitcher.nextFragment(mSavedState);
    }
}
