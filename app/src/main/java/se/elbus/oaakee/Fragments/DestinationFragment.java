package se.elbus.oaakee.Fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.elbus.oaakee.R;
import se.elbus.oaakee.REST_API.VT_Callback;
import se.elbus.oaakee.REST_API.VT_Client;
import se.elbus.oaakee.REST_API.VT_Model.Departure;
import se.elbus.oaakee.REST_API.VT_Model.DepartureBoard;
import se.elbus.oaakee.REST_API.VT_Model.JourneyDetail;
import se.elbus.oaakee.REST_API.VT_Model.LocationList;
import se.elbus.oaakee.REST_API.VT_Model.Stop;
import se.elbus.oaakee.REST_API.VT_Model.StopLocation;

/**
 * Fragment for the "Choose destination"-layout
 * Created by Tobias on 15-09-30.
 */
public class DestinationFragment extends Fragment implements VT_Callback {

    private ListView mDestinationsListView;
    private VT_Client vtClient;

    private Departure mDeparture;
    private JourneyDetail mJourneyDetail;
    private StopLocation mStopLocation;

    private List<Stop> mStops;
    private Stop mPressedStop;

    private FragmentSwitchCallbacks mFragmentSwitcher;
    private Bundle mSavedInformation; // This is to hold the information from previous fragment.

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vtClient = new VT_Client(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null){
            mSavedInformation.putAll(savedInstanceState);
        }

        View v = inflater.inflate(R.layout.fragment_destination, container, false);

        TextView mTransportLineName = (TextView) v.findViewById(R.id.transportLineName);
        TextView mTransportLineDirection = (TextView) v.findViewById(R.id.transportLineDirection);
        TextView mTransportFrom = (TextView) v.findViewById(R.id.transportFromStop);

        mStopLocation = mSavedInformation.getParcelable("source");
        mDeparture = mSavedInformation.getParcelable("trip");

        mTransportLineName.setText(mDeparture.name);
        mTransportLineName.setText(mDeparture.name.substring(getIndexOfFirstDigit(mDeparture.name))); // Remove "Buss", "Spårvagn", etc..
        mTransportLineDirection.setText(mDeparture.direction);
        mTransportFrom.setText(mStopLocation.name.substring(0, mStopLocation.name.indexOf(","))); // Remove ", Göteborg"

        mDestinationsListView = (ListView) v.findViewById(R.id.destinationsListView);
        mDestinationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPressedStop = mStops.get(position);

                Stop destination = mPressedStop;
                JourneyDetail journeyDetails = mJourneyDetail;

                mSavedInformation.putParcelable("destination", destination);
                mSavedInformation.putParcelable("journey", journeyDetails);

                mFragmentSwitcher.nextFragment((Bundle) mSavedInformation.clone());

                Bundle fragment_args = generateBundle();
                mFragmentSwitcher.nextFragment(fragment_args);
            }
        });

        vtClient.get_journey_details(mDeparture.journeyDetailRef);
        mTransportLineName.getBackground().setColorFilter(Color.parseColor(mDeparture.fgColor), PorterDuff.Mode.MULTIPLY);
        mTransportLineDirection.requestFocus();
//        mTransportLineName.setText("999");

        return v;
    }

    /**
     * This will save the information gotten from the previous fragment.
     *
     * @param outState is the bundle we get in onCreate and onCreateView.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putAll(mSavedInformation);
        super.onSaveInstanceState(outState);
    }
    @NonNull
    private Bundle generateBundle() {
        Bundle fragment_args = new Bundle();
        fragment_args.putParcelable("source", mStopLocation);
        fragment_args.putParcelable("destination", mPressedStop);
        fragment_args.putParcelable("trip", mDeparture);
        fragment_args.putParcelable("journey", mJourneyDetail);
        return fragment_args;
    }

    private int getIndexOfFirstDigit(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i)))
                return i;
        }
        return -1;
    }


    /**
     * Populates destination list view with names of stops
     *
     * @param destinations list of strings which will be added to the list view
     */
    private void populateDestinationsList(List<String> destinations) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, destinations);

        mDestinationsListView.setAdapter(adapter);
    }

    // **************************
    // VT_Callback implementation
    // **************************
    @Override
    public void got_journey_details(JourneyDetail journeyDetail) {
        mJourneyDetail = journeyDetail;

        mStops = new ArrayList<>();

        ArrayList<String> destinations = new ArrayList<>();

        boolean currentFound = false;

        for (Stop s : mJourneyDetail.stop) {
            Log.i("### LINE STOPS @", s.name + " WHEN: " + s.arrTime);

            if (s.name.equals(mStopLocation.name)) {
                currentFound = true;
            }

            if (currentFound) {
                destinations.add(s.name.substring(0, s.name.indexOf(",")));
                mStops.add(s);
            }
        }

        // Index zero contains the current stop the user is standing on
        destinations.remove(0);
        mStops.remove(0);

        populateDestinationsList(destinations);
    }

    @Override
    public void got_nearby_stops(LocationList locationList) {

    }

    @Override
    public void got_departure_board(DepartureBoard departureBoard) {

    }

    @Override
    public void got_error(String during_method, String error_msg) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFragmentSwitcher = (FragmentSwitchCallbacks) context;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        mSavedInformation = (Bundle) args.clone();
    }
}
