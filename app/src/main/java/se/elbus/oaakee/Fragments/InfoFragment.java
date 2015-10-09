package se.elbus.oaakee.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import se.elbus.oaakee.AlarmService;
import se.elbus.oaakee.Buses.WifiFinder;
import se.elbus.oaakee.MainActivity;
import se.elbus.oaakee.R;
import se.elbus.oaakee.REST_API.EC_Callback;
import se.elbus.oaakee.REST_API.EC_Client;
import se.elbus.oaakee.REST_API.EC_Model.Bus_info;
import se.elbus.oaakee.REST_API.VT_Callback;
import se.elbus.oaakee.REST_API.VT_Client;
import se.elbus.oaakee.REST_API.VT_Model.DepartureBoard;
import se.elbus.oaakee.REST_API.VT_Model.JourneyDetail;
import se.elbus.oaakee.REST_API.VT_Model.JourneyDetailRef;
import se.elbus.oaakee.REST_API.VT_Model.LocationList;

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
    private TextView textview_arrives;

    private final int UPDATE_TIMER_INTERVAL = 1000; // TODO: Maybe need to adjust
    private Timer vt_update_timer;

    public InfoFragment(){}

    // To create a new instance of this fragment
    // TODO: These arguments will probably change...
    public static InfoFragment newInstance(String origin_stop_ID, String line_ref_URL, String line_short_name, String destination_name){
        InfoFragment infoFragment = new InfoFragment();

        Bundle fragment_args = new Bundle();                // Save the arguments in a bundle in case the state is destroyed and needs to be recreated (like screen rotation)
        fragment_args.putString("origin_stop_ID", origin_stop_ID);
        fragment_args.putString("line_ID", line_ref_URL);
        fragment_args.putString("destination_name", destination_name);
        fragment_args.putString("line_short_name", line_short_name);
        infoFragment.setArguments(fragment_args);

        return infoFragment;
    }


    private void update_gui(){
        if (arrived_at_destination){ // We are at the destination. Update the GUI to show the user.
            textview_arrives.setText(R.string.arrived_at_destination);
        }else{
            if (onBus){
                textview_arrives.setText(R.string.arrive_at_destination);
            }else{
                textview_arrives.setText(R.string.bus_arrives_in);
            }
        }


    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        TextView textview_line_short_name = (TextView) view.findViewById(R.id.infoBusName);
        textview_arrives = (TextView) view.findViewById(R.id.infoArrivesIn);

        Bundle bundle = getArguments();

        if (savedInstanceState != null) {
            Log.i("### info_frag", "has saved instance");
            onBus = savedInstanceState.getBoolean("onBus", false); // If has saved instance restore state. Otherwise assume we are not on the bus.
            arrived_at_destination = savedInstanceState.getBoolean("arrived_at_destination", false); // True if we already are at the destination
        }

        textview_line_short_name.setText(parent.getString(R.string.info_bus_name_prefix) + bundle.getString("line_short_name") + parent.getString(R.string.info_bus_name_suffix)); //TODO: Not beautiful...
        current_journey_ref = new JourneyDetailRef(bundle.getString("line_ID")); // Create a new journey object since it's easier to handle strings in bundle..

        ec_client = new EC_Client(this); // TODO: Remove when get reference from parent
        vt_client = new VT_Client(this); // TODO: Remove when get reference from parent

        if (!arrived_at_destination) { // has not arrived at destination yet.


            if (!onBus) { // Start WiFi-Finder and check if we are on the bus
                Log.i("### info_frag", "is not on bus yet as we know. Checking WiFi");
                wifiFinder = new WifiFinder(parent, parent.getString(R.string.buswifiname)) {
                    @Override
                    // Found an Dgw close to us. We assume we are on this bus

                    public void receiveDgw(String dgw) {
                        Log.i("### info_frag", "Found Wifi, dgw: " + dgw);
                        current_dgw = dgw;
                        onBus = true;
                    }
                };
            }

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







//        onBus = false;

//        Random ran = new Random();
//
//        mTV = (TextView) v.findViewById(R.id.timeTilArrival);
//        mTV.setText(Integer.toString(ran.nextInt(13) + 2));
//        mTV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View vClick) {
//                int minLeft = Integer.parseInt(mTV.getText().toString());
//                if (minLeft <= 0) {
//                    onBus = !onBus;
//                    Random ran = new Random();
//                    mTV.setText(Integer.toString(ran.nextInt(13) + 2));
//                    modifyView();
//                } else {
//                    mTV.setText(Integer.toString(minLeft - 1));
//                }
//            }
//        });

        return view;
    }

//    private void modifyView() {
//        View v = InfoFragment.this.getView();
//        TextView tView = (TextView) v.findViewById(R.id.infoArrivesIn);
//        TextView tBusName = (TextView) v.findViewById(R.id.infoBusName);
//        if(onBus) {
//            tView.setText("framme om");
//        } else {
//            Random ran = new Random();
//            int busNr = ran.nextInt(55) + 1;
//            tBusName.setText(busNr + ":an");
//            tView.setText("ankommer om");
//        }
//
//    }

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
