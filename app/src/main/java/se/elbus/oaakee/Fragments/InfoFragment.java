package se.elbus.oaakee.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import se.elbus.oaakee.MainActivity;
import se.elbus.oaakee.R;
import se.elbus.oaakee.REST_API.VT_Callback;
import se.elbus.oaakee.REST_API.VT_Client;
import se.elbus.oaakee.REST_API.VT_Model.Departure;
import se.elbus.oaakee.REST_API.VT_Model.DepartureBoard;
import se.elbus.oaakee.REST_API.VT_Model.JourneyDetail;
import se.elbus.oaakee.REST_API.VT_Model.LocationList;
import se.elbus.oaakee.REST_API.VT_Model.Stop;
import se.elbus.oaakee.REST_API.VT_Model.StopLocation;
import se.elbus.oaakee.Services.AlarmService;
import se.elbus.oaakee.Services.DetectBusService;

public class InfoFragment extends Fragment implements VT_Callback{

    private boolean onBus;
    private boolean arrived_at_destination;

    private MainActivity parent;
    private VT_Client vt_client; // TODO: Will get reference from parent

    private TextView textView_choosen_trip;
    private TextView textView_arrives_or_departures;
    private TextView textView_counter;
    private TextView textView_below_circle;


    private final int UPDATE_TIMER_INTERVAL = 20000; // TODO: Maybe need to adjust
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
        fragment_args.putParcelable("journeyDetails", journeyDetails);

        infoFragment.setArguments(fragment_args);

        return infoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        DetectBusService.setServiceAlarm(getActivity(), true);
        super.onCreate(savedInstanceState);
    }


    private void update_gui(){
        Stop journey_source = null, journey_destination = null;
        for (Stop s : journeyDetails.stop){
            if (s.id.substring(0,15).equals(source.id.substring(0,15))){ //Using subystring on ID's since last number is different for different tracks and doesn't always match. Don't know why...
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

        if (journey_source.rtArrTime == null){ // If bus has arrived VT will return null as real time data
            onBus = true;
        }

        if (journey_destination.rtArrTime == null){
            arrived_at_destination = true;
        }

        if (arrived_at_destination){ // We are at the destination. Update the GUI to show the user.
            textView_arrives_or_departures.setText(R.string.arrived_at_destination);
        }else{
            if (onBus){
                textView_arrives_or_departures.setText(R.string.arrive_at_destination);
                long minutes_left = time_diff_minutes(journey_destination.arrDate, journey_destination.rtArrTime);
                textView_counter.setText( minutes_left == -1 ? "?" :  Long.toString(minutes_left)  );
                textView_below_circle.setText("vid " + journey_destination.name);
            }else{
                textView_arrives_or_departures.setText(R.string.arrives_in);
                long minutes_left = time_diff_minutes(journey_source.arrDate, journey_source.rtArrTime);
                textView_counter.setText( minutes_left == -1 ? "?" :  Long.toString(minutes_left)  );
                textView_below_circle.setText("till " + journey_source.name + " (Läge " + journey_source.track + ")");
            }
        }
    }


    private long time_diff_minutes(String date_1, String time_1){
        SimpleDateFormat input_format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date input_date = input_format.parse(date_1 + " " + time_1);
            Date vasttrafik_server_date = input_format.parse(journeyDetails.serverdate + " " + journeyDetails.servertime);

            long diffInMilliseconds = input_date.getTime() -  vasttrafik_server_date.getTime();
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMilliseconds);
            if (diffInMinutes < 0){ // We don't want to show -1 and stuff to the user
                diffInMinutes = 0;
            }
            return diffInMinutes;

        } catch (ParseException e) {
            return -1;
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
        textView_below_circle = (TextView) view.findViewById(R.id.below_circle);

        View circle =  view.findViewById(R.id.infoCircleHolder);
//        circle.setBackgroundColor(Color.parseColor(departure_from_board.bgColor) );
//        textView_counter.setTextColor(Color.parseColor(journeyDetails.color.fgColor));

//        Log.i("### COLOR", departure_from_board.bgColor);

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

        textview_line_short_name.setText( departure_from_board.name );

        vt_client = new VT_Client(this); // TODO: Remove when/if get reference from parent

        update_gui(); // Update GUI once since the timer will wait a defined amount of seconds until it starts

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

                        }
                    });
                    vt_client.get_journey_details(departure_from_board.journeyDetailRef);

                }
            }, 0, UPDATE_TIMER_INTERVAL);

        }else{
            Log.i("### info_frag", "has arrived at destination");

        }

        return view;
    }


    @Override
    public void got_journey_details(JourneyDetail journeyDetail) {
        journeyDetails = journeyDetail;
        Log.i("### INFO", "Got new journeydetails. Updating GUI. ServerTIME: " + journeyDetail.serverdate + " " + journeyDetail.servertime);
        update_gui();
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

    public static Intent newIntent(Context context) {
        return new Intent(context, AlarmService.class);
    }
}
