package se.elbus.oaakee.Fragments;

import android.content.Context;
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
    private VT_Client vt_client; // TODO: Will maybe get reference from parent

    private TextView textView_choosen_trip;
    private TextView textView_arrives_or_departures;
    private TextView textView_counter;
    private TextView textView_below_circle;
    private TextView textView_minutes_text;

    private View circle;


    private final int UPDATE_TIMER_INTERVAL = 20000; // TODO: Maybe need to adjust
    private Timer vt_update_timer;


    private StopLocation source;
    private Stop destination;
    private Departure departure_from_board;
    private JourneyDetail journeyDetails;


    private boolean use_source_timetable, use_destination_timetable;

    // Update the GUI to show TIME and STOP information
    private void update_gui(){
        Stop journey_source = null, journey_destination = null;

        // Check every stop for our SOURCE and DESTINATION
        for (Stop s : journeyDetails.stop){ // TODO ****************************************************** SOMETHING IS WRONG. NO USE SUBSTRING
            if (s.id.substring(0,14).equals(source.id.substring(0,14))){ //Using subystring on ID's since last number is different for different tracks and doesn't always match. Don't know why...
                journey_source = s;
            }
            if (s.id.substring(0,14).equals(destination.id.substring(0,14))){
                journey_destination = s;
            }
        }

        // ***** check if on bus OR get SOURCE time
        if (journey_source != null) {
            Log.i("### INFO SRC", journey_source.name + " RT ARRIVAL TIME: " + journey_source.rtArrTime + " TIMETABLE: " + journey_source.arrTime);


            if (journey_source.rtArrTime == null) { // If bus has arrived at source || no real time data is available

                Log.i("### INFO SRC", "NO RT, checking timetable diff: " + vt_time_diff_minutes(journey_source.arrDate, journey_source.arrTime));
                if (vt_time_diff_minutes(journey_source.arrDate, journey_source.arrTime) <= 0) { // Check timetable so we know if real time data is unavailable  if we really are on the bus
                    onBus = true;
                    use_source_timetable = false;
                } else { // Real time data is unavailable for source
                    use_source_timetable = true;
                }

            }
        }else{
            Log.e("### INFO", "BAD SOURCE");
            return;
        }

        // ***** check if arrived to our stop OR get DESTINATION time
        if (journey_destination != null) {
            Log.i("### INFO DEST", destination.name + " RT ARRIVAL TIME " + journey_destination.rtArrTime + " TIMETABLE: " + journey_destination.arrTime);
            if (journey_destination.rtArrTime == null) {

                Log.i("### INFO DEST", "NO RT, checking timetable diff: " + vt_time_diff_minutes(journey_destination.arrDate, journey_destination.arrTime));
                if (vt_time_diff_minutes(journey_destination.arrDate, journey_destination.arrTime) <= 0) { // Check timetable so we know if real time data is unavailable if we really have arrived at the destination
                    arrived_at_destination = true;
                    use_destination_timetable = false;
                } else { // Real time data is unavailable for destination
                    use_destination_timetable = true;
                }
            }
        }else{
            Log.e("### INFO", "BAD DESTINATION");
            return;
        }


        if (arrived_at_destination){ // We are at the destination. Update the GUI to show the user.
            textView_arrives_or_departures.setText(R.string.arrived_at_destination); // "Arrived at destination" string
            vt_update_timer.cancel(); // Stop the timer since we don't need it no more
            // Hide circle and its contents
//            circle.setVisibility(View.INVISIBLE);
            textView_counter.setVisibility(View.INVISIBLE);
            textView_minutes_text.setVisibility(View.INVISIBLE);
            textView_below_circle.setText(journey_destination.name); // Only show name of destination this time. No text before.


        }else{
            long minutes_left;

            if (onBus){ // We are on the bus. Show time until we arrive at the destination
                textView_arrives_or_departures.setText(R.string.arrive_at_destination); // "Arrive at destination" string

                minutes_left = use_destination_timetable ? vt_time_diff_minutes(journey_destination.arrDate, journey_destination.arrTime) :
                        vt_time_diff_minutes(journey_destination.rtArrDate, journey_destination.rtArrTime);

                textView_below_circle.setText(parent.getString(R.string.to) + " " + journey_destination.name);

            }else{ // We are not on the bus yet. Show time until it arrives to our stop
                textView_arrives_or_departures.setText(R.string.departures_in); // "departures in" string
                minutes_left = use_source_timetable ?  vt_time_diff_minutes(journey_source.depDate, journey_source.depTime) :
                        vt_time_diff_minutes(journey_source.rtDepDate, journey_source.rtDepTime);

                textView_below_circle.setText(parent.getString(R.string.from) + " " + journey_source.name + " (" + parent.getString(R.string.track) + " " + journey_source.track + ")" );
            }

            if (minutes_left < 0){
                minutes_left = 0; // Don't show -1 to the user
            }
            textView_counter.setText( minutes_left == -1 ? "?" :  Long.toString(minutes_left)  );
            if (minutes_left == 1){
                textView_minutes_text.setText(R.string.minute);
            }else{
                textView_minutes_text.setText(R.string.minutes);
            }

        }
    }


    // Helper method to show de difference between a [date , time] compared to Vasttrafik server [date , time]
    // We don't want to rely on that the local clock matches the servers, therefore we use the data that is always supplied from Vasttrafik
    private long vt_time_diff_minutes(String date_1, String time_1){
        SimpleDateFormat input_format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date input_date = input_format.parse(date_1 + " " + time_1);
            Date vasttrafik_server_date = input_format.parse(journeyDetails.serverdate + " " + journeyDetails.servertime);

            long diffInMilliseconds = input_date.getTime() -  vasttrafik_server_date.getTime();
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMilliseconds);

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
        savedInstanceState.putParcelable("journeyDetails_updated", journeyDetails);
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
        textView_minutes_text = (TextView) view.findViewById(R.id.info_minutes_text);
        textView_arrives_or_departures = (TextView) view.findViewById(R.id.infoArrivesIn);
        textView_below_circle = (TextView) view.findViewById(R.id.below_circle);

        circle =  view.findViewById(R.id.infoCircleHolder);
//        circle.setBackgroundColor(Color.parseColor(departure_from_board.bgColor) );
//        textView_counter.setTextColor(Color.parseColor(journeyDetails.color.fgColor));

//        Log.i("### COLOR", departure_from_board.bgColor);


        // Load the arguements from newInstance
        Bundle bundle = getArguments();
        source = bundle.getParcelable("source");
        destination = bundle.getParcelable("destination");
        departure_from_board = bundle.getParcelable("trip");
        journeyDetails = bundle.getParcelable("journey"); // Use this if no newer is stored in savedState

        textView_choosen_trip.setText(source.name + " - " + destination.name);

        if (savedState != null) {
            Log.i("### info_frag", "has saved instance");
            onBus = savedState.getBoolean("onBus", false); // If has saved instance restore state. Otherwise assume we are not on the bus.
            arrived_at_destination = savedState.getBoolean("arrived_at_destination", false); // True if we already are at the destination
            journeyDetails = savedState.getParcelable("journeyDetails_updated");
        }

        textview_line_short_name.setText( departure_from_board.name );

        if (journeyDetails.color != null){
            textview_line_short_name.setTextColor(Color.parseColor(journeyDetails.color.fgColor));

            if (!journeyDetails.color.bgColor.equals("#ffffff")){ // White looks ugly as background when fragment bakground is gray
                textview_line_short_name.setBackgroundColor(Color.parseColor(journeyDetails.color.bgColor));
            }
        }

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
                    Log.i("### INFO", "TIMER EVENT, Checking Västtrafik API");

                    // When you need to modify a UI element, do so on the UI thread.
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.

                        }
                    });
                    vt_client.get_journey_details(departure_from_board.journeyDetailRef);

                    //Check DetectBusService to see if we're on the bus
                    if(!onBus) {
                        if(DetectBusService.onBus){
                            AlarmService.setServiceAlarm(getActivity(), true, DetectBusService.dgwFound, destination.name);
                            onBus = true;
                        }
                    }
                }
            }, 0, UPDATE_TIMER_INTERVAL);

        }else{
            Log.i("### INFO", "has arrived at destination");

        }
        DetectBusService.setServiceAlarm(getActivity(), true);

        return view;
    }


    @Override
    public void got_journey_details(JourneyDetail journeyDetail) {
        journeyDetails = journeyDetail;
        Log.i("### INFO", "Got new journeydetails. Updating GUI. ServerTIME: " + journeyDetail.serverdate + " " + journeyDetail.servertime );



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

    //For alarm functionality, add this
    //AlarmService.setServiceAlarm(getActivity(), true);
}
