package se.elbus.oaakee.fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
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
import se.elbus.oaakee.fragments.presenter.ITravelPresenter;
import se.elbus.oaakee.fragments.presenter.TravelPresenterImpl;
import se.elbus.oaakee.restapi.VTCallback;
import se.elbus.oaakee.restapi.VTClient;
import se.elbus.oaakee.restapi.vtmodel.Departure;
import se.elbus.oaakee.restapi.vtmodel.DepartureBoard;
import se.elbus.oaakee.restapi.vtmodel.JourneyDetail;
import se.elbus.oaakee.restapi.vtmodel.LocationList;
import se.elbus.oaakee.restapi.vtmodel.StopLocation;

public class TravelFragment extends Fragment implements VTCallback, AdapterView.OnItemSelectedListener, ITravelView {

    protected static final String TAG = "Travel";

    private Spinner mBusStops;

    private ArrayAdapter<String> mDepartureListAdapter;
    private ArrayAdapter<List<Departure>> mDeparturesAdapter;

    private final List<List<Departure>> mDeparturesSorted = new ArrayList<>();
    private final List<StopLocation> mBusStopList = new ArrayList<>(); //With removed duplicates

    private VTClient mVTClient;
    private FragmentSwitchCallbacks mFragmentSwitcher;
    private Bundle mSavedState;

    private ITravelPresenter mPresenter;

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
        mPresenter = new TravelPresenterImpl(this);
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
        ListView mDeparturesList = (ListView) v.findViewById(R.id.departuresListView);
        mDeparturesList.setAdapter(mDeparturesAdapter);

        return v;
    }
    @Override
    public void onStart() {
        super.onStart();
        mPresenter.updateModelLocation(getContext());
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
    public void saveParcelable(String key, Parcelable parcelable) {
        mSavedState.putParcelable(key,parcelable);
    }

    @Override
    public void nextFragment() {
        mFragmentSwitcher.nextFragment(mSavedState);
    }

    @Override
    public void warnGPSOff() {
        Toast.makeText(getContext(), R.string.gps_off_warning_text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateLocation(Location location) {
        mVTClient.getNearbyStops(location.getLatitude() + "", location.getLongitude() + "", "30", "1000");
    }
}
