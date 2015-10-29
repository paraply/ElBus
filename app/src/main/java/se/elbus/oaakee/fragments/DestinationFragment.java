package se.elbus.oaakee.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
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
import se.elbus.oaakee.restapi.VTCallback;
import se.elbus.oaakee.restapi.VTClient;
import se.elbus.oaakee.restapi.vtmodel.Departure;
import se.elbus.oaakee.restapi.vtmodel.DepartureBoard;
import se.elbus.oaakee.restapi.vtmodel.JourneyDetail;
import se.elbus.oaakee.restapi.vtmodel.LocationList;
import se.elbus.oaakee.restapi.vtmodel.Stop;
import se.elbus.oaakee.restapi.vtmodel.StopLocation;

/**
 * Fragment for the "Choose destination"-layout
 */
public class DestinationFragment extends Fragment implements VTCallback {

    private ArrayAdapter mDestinationsListAdapter;
    private VTClient mVTClient;

    private JourneyDetail mJourneyDetail;
    private StopLocation mStopLocation;

    private List<Stop> mStops;
    private Stop mPressedStop;

    private FragmentSwitchCallbacks mFragmentSwitcher;
    private Bundle mSavedInformation; // This is to hold the information from previous fragment.

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVTClient = new VTClient(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mSavedInformation.putAll(savedInstanceState);
        }

        View v = inflater.inflate(R.layout.fragment_destination, container, false);

        TextView mTransportLineName = (TextView) v.findViewById(R.id.transportLineName);
        TextView mTransportLineDirection = (TextView) v.findViewById(R.id.transportLineDirection);
        TextView mTransportFrom = (TextView) v.findViewById(R.id.transportFromStop);

        mStopLocation = mSavedInformation.getParcelable("source");
        Departure mDeparture = mSavedInformation.getParcelable("trip");

        mTransportLineName.setText(mDeparture.sname);

        if (mTransportLineName.getText().length() >= 4) {
            mTransportLineName.setTextSize(12);
        }

        mTransportLineDirection.setText(mDeparture.direction);

        // Remove ", [name]"
        mTransportFrom.setText(mStopLocation.name.substring(0, mStopLocation.name.indexOf(",")));

        ListView mDestinationsListView = (ListView) v.findViewById(R.id.destinationsListView);
        mDestinationsListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        mDestinationsListView.setAdapter(mDestinationsListAdapter);

        mDestinationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPressedStop = mStops.get(position);

                Stop destination = mPressedStop;
                JourneyDetail journeyDetails = mJourneyDetail;

                mSavedInformation.putParcelable("destination", destination);
                mSavedInformation.putParcelable("journey", journeyDetails);

                mFragmentSwitcher.nextFragment((Bundle) mSavedInformation.clone());
            }
        });

        mVTClient.getJourneyDetails(mDeparture.journeyDetailRef);

        String white = "#ffffff";
        String backgroundColor = mDeparture.fgColor;
        mTransportLineName.setTextColor(Color.parseColor(white));
        Log.i("DEST", "Color white: " + mDeparture.fgColor + ", " + mDeparture.sname);
        if (mDeparture.fgColor.equals(white)) {
            backgroundColor = "#DBDBDB"; //grey if background white
        }
        mTransportLineName.getBackground().setColorFilter(Color.parseColor(backgroundColor), PorterDuff.Mode.MULTIPLY);

        mTransportLineDirection.requestFocus(); // Make the line direction scroll if necessary

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

    @Override
    public void handleJourneyDetails(JourneyDetail journeyDetail) {
        mDestinationsListAdapter.clear();
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

        if (!destinations.isEmpty()) {
            destinations.remove(0);
            mStops.remove(0);
        }

        mDestinationsListAdapter.addAll(destinations);
        mDestinationsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void handleNearbyStops(LocationList locationList) {

    }

    @Override
    public void handleDepartureBoard(DepartureBoard departureBoard) {

    }

    @Override
    public void handleError(String during_method, String error_msg) {

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
