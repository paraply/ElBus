package se.elbus.oaakee.Fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import se.elbus.oaakee.R;
import se.elbus.oaakee.REST_API.VT_Callback;
import se.elbus.oaakee.REST_API.VT_Client;
import se.elbus.oaakee.REST_API.VT_Model.Departure;
import se.elbus.oaakee.REST_API.VT_Model.DepartureBoard;
import se.elbus.oaakee.REST_API.VT_Model.JourneyDetail;
import se.elbus.oaakee.REST_API.VT_Model.JourneyDetailRef;
import se.elbus.oaakee.REST_API.VT_Model.LocationList;
import se.elbus.oaakee.REST_API.VT_Model.Stop;
import se.elbus.oaakee.REST_API.VT_Model.StopLocation;

/**
 * Fragment for the "Choose destination"-layout
 * <p/>
 * Created by Tobias on 15-09-30.
 */
public class DestinationFragment extends Fragment implements VT_Callback {

    private TextView mTransportLineName;
    private TextView mTransportLineDirection;
    private TextView mTransportFrom;
    private ListView mDestinationsListView;
    private VT_Client vtClient;

    // Assumed data from TravelFragment
    private StopLocation mCurrentStop;  // The stop the user is currently standing on
    private JourneyDetail mLine;        // The line the user wants to ride

    private DepartureBoard mDepartureBoard;
    private LocationList mLocationList;
    private JourneyDetailRef mJourneyDetailRef;
    private JourneyDetail mJourneyDetail;

    private List<Stop> mStops;
    private Stop mPressedStop;

    // Test data
    private double latitude = 57.692395;
    private double longitude = 11.972917;

    private String kapellplatsenIDA = "9022014003760001";
    private String kapellplatsenIDB = "9022014003760002";
    private String testName = "7";
    private String testDirection = "Tynnered";
    private String testStop = "Kapellplatsen";

    private FragmentSwitchCallbacks mFragmentSwithcer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vtClient = new VT_Client(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_destination, container, false);

        mTransportLineName = (TextView) v.findViewById(R.id.transportLineName);
        mTransportLineDirection = (TextView) v.findViewById(R.id.transportLineDirection);
        mTransportFrom = (TextView) v.findViewById(R.id.transportFromStop);

        mDestinationsListView = (ListView) v.findViewById(R.id.destinationsListView);
        mDestinationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object selected = parent.getItemAtPosition(position);
                Log.i("TAG", selected.toString());
                getItemFromDestinationList(position);
                mFragmentSwithcer.nextFragment(null);
            }
        });

        vtClient.get_departure_board(kapellplatsenIDA);

        mTransportLineName.setText(testName);
        mTransportLineDirection.setText(testDirection);
        mTransportFrom.setText(testStop);

        return v;
    }

    private void getItemFromDestinationList(int position) {
        mPressedStop = mStops.get(position);
    }

    /**
     * Populates destination list view with example names of stops
     */
    private void populateDestinationsListExamples() {
        String destinations[] = {"Kapellplatsen", "Götaplatsen", "Valand", "Kungsportsplatsen", "Domkyrkan", "Lilla Bommen"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, destinations);
        mDestinationsListView.setAdapter(adapter);
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

            if (s.name.equals("Kapellplatsen, Göteborg")) {
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
        mDepartureBoard = departureBoard;

        for (Departure d : mDepartureBoard.departure) { // List all the departures from this stop
            Log.i("### DEPARTURES: ", d.name + " SHORT NAME: " + d.sname + " DIRECTION: " + d.direction);

            if (d.sname.equals(testName) && d.direction.equals(testDirection)) {
                mJourneyDetailRef = d.journeyDetailRef;
                vtClient.get_journey_details(mJourneyDetailRef);
            }
        }
    }

    @Override
    public void got_error(String during_method, String error_msg) {

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFragmentSwithcer = (FragmentSwitchCallbacks) context;
    }
}
