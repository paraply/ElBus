package se.elbus.oaakee.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import se.elbus.oaakee.Buses.WifiFinder;
import se.elbus.oaakee.MainActivity;
import se.elbus.oaakee.R;
import se.elbus.oaakee.REST_API.EC_Callback;
import se.elbus.oaakee.REST_API.EC_Client;
import se.elbus.oaakee.REST_API.EC_Model.Bus_info;
import se.elbus.oaakee.REST_API.VT_Callback;
import se.elbus.oaakee.REST_API.VT_Client;
import se.elbus.oaakee.REST_API.VT_Model.Departure;
import se.elbus.oaakee.REST_API.VT_Model.DepartureBoard;
import se.elbus.oaakee.REST_API.VT_Model.JourneyDetail;
import se.elbus.oaakee.REST_API.VT_Model.JourneyDetailRef;
import se.elbus.oaakee.REST_API.VT_Model.LocationList;
import se.elbus.oaakee.REST_API.VT_Model.Stop;
import se.elbus.oaakee.REST_API.VT_Model.StopLocation;

public class InfoFragment extends Fragment implements EC_Callback,VT_Callback{

    private TextView mTV;
    private boolean onBus;
    private boolean arrived_at_destination;

    private MainActivity parent;
    private WifiFinder wifiFinder;
    private EC_Client ec_client; // TODO: Maybe will get reference from parent
    private String current_dgw;
    private VT_Client vt_client; // TODO: Will get reference from parent
    private JourneyDetailRef current_journey_ref;

    private TextView textView_choosen_trip;
    private TextView textView_arrives_or_departures;
    private TextView textView_counter;

    private String time_string_from_vt;

    private final int UPDATE_TIMER_INTERVAL = 10000; // TODO: Maybe need to adjust
    private Timer vt_update_timer;


    private StopLocation source;
    private Stop destination;
    private Departure departure_from_board;
    private JourneyDetail journeyDetails;



    public static InfoFragment newInstance(StopLocation source, Stop destination, Departure departure_from_board, JourneyDetail journeyDetails){
        InfoFragment infoFragment = new InfoFragment();
        Bundle fragment_args = new Bundle();                // Save the arguments in a bundle in case the state is destroyed and needs to be recreated (like screen rotation)
        fragment_args.putParcelable("source", source);
        fragment_args.putParcelable("destination", destination);
        fragment_args.putParcelable("departure_from_board", departure_from_board);
        fragment_args.putParcelable("journeyDetails",journeyDetails);

        infoFragment.setArguments(fragment_args);

        return infoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
    }


    private void update_gui(){
        Stop journey_source = null, journey_destination = null;
        for (Stop s : journeyDetails.stop){
            if (s.id.substring(0,15).equals(source.id.substring(0,15))){
                journey_source = s;
            }
            if (s.id.substring(0,15).equals(destination.id.substring(0,15))){
                journey_destination = s;
            }
        }

        if (journey_source != null){
            Log.i("### GUI SRC",  journey_source.name + " RT ARRIVAL TIME: " + journey_source.rtArrTime);
        }
        if (journey_destination != null){
            Log.i("### GUI DEST", destination.name + " RT ARRIVAL TIME " + journey_destination.rtArrTime);
        }



        if (arrived_at_destination){ // We are at the destination. Update the GUI to show the user.
            textView_arrives_or_departures.setText(R.string.arrived_at_destination);
        }else{
            textView_counter.setText(destination.arrTime);
            if (onBus){
                textView_arrives_or_departures.setText(R.string.arrive_at_destination);
            }else{
                textView_arrives_or_departures.setText(R.string.arrives_in);
            }
        }


    }



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){ // Save the state in case the fragment gets destroyed
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("onBus", onBus);
        savedInstanceState.putBoolean("arrived_at_destination", arrived_at_destination);
    }

    // Called when a fragment is first attached to its context
    // Used to get reference to parent activity
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        parent = (MainActivity) getActivity();
    }

    @Override
    public void onDestroy(){ // Kill the timer when closing fragment
        vt_update_timer.cancel();
        vt_update_timer.purge();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        TextView textview_line_short_name = (TextView) view.findViewById(R.id.infoBusName);

        textView_choosen_trip = (TextView) view.findViewById(R.id.info_from_to);
        textView_counter = (TextView) view.findViewById(R.id.timeTilArrival);
        textView_arrives_or_departures = (TextView) view.findViewById(R.id.infoArrivesIn);

        Bundle bundle = getArguments();

        source = bundle.getParcelable("source");
        destination = bundle.getParcelable("destination");
        departure_from_board = bundle.getParcelable("departure_from_board");
        journeyDetails = bundle.getParcelable("journeyDetails");

        textView_choosen_trip.setText(source.name + " - " + destination.name);

        if (savedState != null) {
            Log.i("### info_frag", "has saved instance");
            onBus = savedState.getBoolean("onBus", false); // If has saved instance restore state. Otherwise assume we are not on the bus.
            arrived_at_destination = savedState.getBoolean("arrived_at_destination", false); // True if we already are at the destination

        }

        textview_line_short_name.setText(parent.getString(R.string.info_bus_name_prefix) + departure_from_board.name + parent.getString(R.string.info_bus_name_suffix)); //TODO: Not beautiful...
        current_journey_ref = new JourneyDetailRef(bundle.getString("line_ID")); // Create a new journey object since it's easier to handle strings in bundle..

        ec_client = new EC_Client(this); // TODO: Remove when get reference from parent
        vt_client = new VT_Client(this); // TODO: Remove when get reference from parent

        if (!arrived_at_destination) { // has not arrived at destination yet.


//            if (!onBus) { // Start WiFi-Finder and check if we are on the bus
//                Log.i("### info_frag", "is not on bus yet as we know. Checking WiFi");
//                wifiFinder = new WifiFinder(parent, parent.getString(R.string.buswifiname)) {
//                    @Override
//                    // Found an Dgw close to us. We assume we are on this bus
//
//                    public void receiveDgw(String dgw) {
//                        Log.i("### info_frag", "Found Wifi, dgw: " + dgw);
//                        current_dgw = dgw;
//                        onBus = true;
//                    }
//                };
//            }

            //  Create a timer that will update the status
            vt_update_timer = new Timer();
            vt_update_timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.i("### info_frag", "TIMER EVENT, Checking Västtrafik API");

                    // When you need to modify a UI element, do so on the UI thread.
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            update_gui();
                        }
                    });
//                    vt_client.get_journey_details(current_journey_ref);

                }
            }, 0, UPDATE_TIMER_INTERVAL);

        }else{
            Log.i("### info_frag", "has arrived at destination");

        }

        return view;
    }


    @Override
    public void got_sensor_data(List<Bus_info> bus_info) {

    }

    @Override
    public void got_sensor_data_from_all_buses(List<Bus_info> bus_info) {

    }

    @Override
    public void got_reource_data(List<Bus_info> bus_info) {

    }

    @Override
    public void got_reource_data_from_all_buses(List<Bus_info> bus_info) {

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

    //For alarm functionality, add this
    //AlarmService.setServiceAlarm(getActivity(), true);
}
