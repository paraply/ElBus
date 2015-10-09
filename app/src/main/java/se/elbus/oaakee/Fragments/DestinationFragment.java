package se.elbus.oaakee.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import se.elbus.oaakee.R;
import se.elbus.oaakee.REST_API.VT_Callback;
import se.elbus.oaakee.REST_API.VT_Client;
import se.elbus.oaakee.REST_API.VT_Model.DepartureBoard;
import se.elbus.oaakee.REST_API.VT_Model.JourneyDetail;
import se.elbus.oaakee.REST_API.VT_Model.LocationList;

/**
 * Fragment for the "Choose destination"-layout
 *
 * Created by Tobias on 15-09-30.
 */
public class DestinationFragment extends Fragment implements VT_Callback {

    private ListView mDestinationsListView;
    private VT_Client vtClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vtClient = new VT_Client(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.destination_chooser, container, false);

        mDestinationsListView = (ListView) v.findViewById(R.id.destinationsListView);

        populateDestinationsList();

        return v;
    }

    /**
     * Populates destination list
     *
     */
    private void populateDestinationsList(){
        String destinations[] = {"Kapellplatsen", "Götaplatsen", "Valand", "Kungsportsplatsen", "Domkyrkan", "Lilla Bommen"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,destinations);
        mDestinationsListView.setAdapter(adapter);
    }

    @Override
    public void got_journey_details(JourneyDetail journeyDetail) {

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
}
