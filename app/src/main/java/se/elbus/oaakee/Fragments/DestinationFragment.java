package se.elbus.oaakee.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
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
 * <p/>
 * Created by Tobias on 15-09-30.
 */
public class DestinationFragment extends Fragment implements VT_Callback {

    private ListView mDestinationsListView;
    private VT_Client vtClient;

    private String kapellplatsenIDA = "9022014003760001";
    private String kapellplatsenIDB = "9022014003760002";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vtClient = new VT_Client(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.destination_chooser, container, false);

        mDestinationsListView = (ListView) v.findViewById(R.id.destinationsListView);

        /*
        double latitude = 57.692395;
        double longitude = 11.972917;

        vtClient.get_nearby_stops(latitude + "", longitude + "", "3", "1000");
        */

        vtClient.get_departure_board(kapellplatsenIDA);

        return v;
    }

    /**
     * Populates destination list
     */
    private void populateDestinationsListExamples() {
        String destinations[] = {"Kapellplatsen", "Götaplatsen", "Valand", "Kungsportsplatsen", "Domkyrkan", "Lilla Bommen"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, destinations);
        mDestinationsListView.setAdapter(adapter);
    }

    private void populateDestinationsList(List<String> destinations) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, destinations);

        mDestinationsListView.setAdapter(adapter);
    }

    @Override
    public void got_journey_details(JourneyDetail journeyDetail) {
        ArrayList<String> destinations = new ArrayList<>();

        for (Stop s : journeyDetail.stop) {
            Log.i("### LINE STOPS @", s.name + " WHEN: " + s.arrTime);
            destinations.add(s.name.substring(0, s.name.indexOf(",")));
        }

        populateDestinationsList(destinations);
    }

    @Override
    public void got_nearby_stops(LocationList locationList) {
        for (StopLocation s : locationList.stoplocation) { // List all nearby stops
            Log.i("### NEAR STOP", s.name + " ID:" + s.id + " TRACK:" + s.track);
        }
        StopLocation closest = locationList.stoplocation.get(0); // The closest stop is at the top of the list
        Log.i("### CLOSEST STOP", closest.name + " ID:" + closest.id + " TRACK:" + closest.track);

    }

    @Override
    public void got_departure_board(DepartureBoard departureBoard) {
        for (Departure d : departureBoard.departure) { // List all the departures from this stop
            Log.i("### DEPARTURES: ", d.name  + " SHORT NAME: " + d.sname + " DIRECTION: " + d.direction);

            if (d.sname.equals("7") && d.direction.equals("Tynnered")){
                vtClient.get_journey_details(d.journeyDetailRef);
                return;
            }
        }
    }

    @Override
    public void got_error(String during_method, String error_msg) {

    }
}
