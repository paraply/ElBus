package se.elbus.oaakee.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import se.elbus.oaakee.MainActivity;
import se.elbus.oaakee.R;
import se.elbus.oaakee.REST_API.ECCallback;
import se.elbus.oaakee.REST_API.ECClient;
import se.elbus.oaakee.REST_API.EC_Model.Bus_info;
import se.elbus.oaakee.REST_API.VTCallback;
import se.elbus.oaakee.REST_API.VTClient;
import se.elbus.oaakee.REST_API.VT_Model.Departure;
import se.elbus.oaakee.REST_API.VT_Model.DepartureBoard;
import se.elbus.oaakee.REST_API.VT_Model.JourneyDetail;
import se.elbus.oaakee.REST_API.VT_Model.LocationList;
import se.elbus.oaakee.REST_API.VT_Model.Stop;
import se.elbus.oaakee.REST_API.VT_Model.StopLocation;
import se.elbus.oaakee.Services.AlarmService;
import se.elbus.oaakee.Services.DetectBusService;

public class InfoFragment extends Fragment implements VTCallback, ECCallback {

    private final int VT_UPDATE_TIMER_INTERVAL = 20000; // Update Västtrafik every 20000 ms
    private final int EC_UPDATE_TIMER_INTERVAL = 10000; // Update Electricity every 10000 ms
    private boolean onBus;
    private boolean got_bus_info_from_wifi;
    private boolean arrived_at_destination;
    private MainActivity parent;
    private VTClient vt_client;
    private ECClient ec_client;
    private TextView textView_above_circle;
    private TextView textView_counter;
    private TextView textView_minutes_text;
    private TextView textView_center_text;
    private Button stop_circle;
    private Timer vt_update_timer;
    private Timer ec_update_timer;


    private StopLocation source;
    private Stop destination;
    private Departure departure_from_board;
    private JourneyDetail journeyDetails;

    private boolean use_source_timetable, use_destination_timetable;


    private void hide_stop_button() {
        stop_circle.setVisibility(View.INVISIBLE);
    }

    private void show_stop_button() {
        stop_circle.setVisibility(View.VISIBLE);
    }

    private void stop_button_state(boolean pressed) {
        stop_circle.setPressed(pressed);
    }

    // Update the GUI to show TIME and STOP information
    private void update_gui() {
        Stop journey_source = null, journey_destination = null;

        if (journeyDetails == null) {
            Toast.makeText(parent, "Error: No details of this journey", Toast.LENGTH_LONG).show();
            return;
        }

        // Compare names from source stop and destination stop
        // with the results from journeyDetails
        for (Stop s : journeyDetails.stop) {
            if (s.name.equals(source.name)) { // Found a match with source stop and a journey details stop
                journey_source = s;
                Log.i("### INFO", "Found src match stop");
            } else if (s.name.equals(destination.name)) { // Found a match with destination stop and a journey details stop
                journey_destination = s;
                Log.i("### INFO", "Found dst match stop");
            }
        }


        if (!onBus) { // We are not on the bus yet according to Västtrafik OR ElectriCity WiFi-finder
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
            } else {
                Log.e("### INFO", "BAD SOURCE, NO STOP NAME MATCHED");
                Toast.makeText(parent, "Error: Cannot find source stop on this journey", Toast.LENGTH_LONG).show();
                return;
            }
        }

        // ***** check if arrived at our stop OR get DESTINATION time
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
        } else {
            Log.e("### INFO", "BAD DESTINATION, NO STOP NAME MATCHED");
            Toast.makeText(parent, "Error: Cannot find destination stop on this journey", Toast.LENGTH_LONG).show();
            return;
        }


        if (arrived_at_destination) { // We are at the destination. Update the GUI to show the user.
            hide_stop_button();
            textView_above_circle.setVisibility(View.INVISIBLE);
            if (vt_update_timer != null) {
                vt_update_timer.cancel(); // Stop the timer since we don't need it no more
            }
            if (ec_update_timer != null) {
                ec_update_timer.cancel();
            }
            textView_counter.setVisibility(View.INVISIBLE);
            textView_minutes_text.setVisibility(View.INVISIBLE);
            textView_center_text.setVisibility(View.VISIBLE);
            textView_center_text.setText(R.string.ARRIVED);
            textView_center_text.setTextSize(45);

        } else {


            long minutes_left;

            if (onBus) { // We are on the bus. Show time until we arrive at the destination
                show_stop_button();
                textView_above_circle.setText(R.string.arrive_at_destination); // "Arrive at destination" string

                minutes_left = use_destination_timetable ? vt_time_diff_minutes(journey_destination.arrDate, journey_destination.arrTime) :
                        vt_time_diff_minutes(journey_destination.rtArrDate, journey_destination.rtArrTime);


            } else { // We are not on the bus yet. Show time until it arrives to our stop
                hide_stop_button();

                minutes_left = use_source_timetable ? vt_time_diff_minutes(journey_source.depDate, journey_source.depTime) :
                        vt_time_diff_minutes(journey_source.rtDepDate, journey_source.rtDepTime);

            }

            show_minutes(minutes_left);

        }
    }

    // Show the counter e.g "1 minute" or "2 minutes" and "departures" or "arrives" above the circle
    private void show_minutes(long minutes_left) {
        if (minutes_left <= 0) {
            if (!onBus) {
                textView_above_circle.setText(R.string.departures); // "departures" string
            } else {
                textView_above_circle.setText(R.string.arrives); // "arrives" string
            }
            textView_counter.setVisibility(View.INVISIBLE);
            textView_minutes_text.setVisibility(View.INVISIBLE);
            textView_center_text.setVisibility(View.VISIBLE);
            textView_center_text.setText(R.string.NOW);
            textView_center_text.setTextSize(70);
        } else {
            if (!onBus) {
                textView_above_circle.setText(R.string.departures_in); // "departures in" string
            } else {
                textView_above_circle.setText(R.string.arrives_in); // "arrives in" string
            }
            textView_counter.setVisibility(View.VISIBLE);
            textView_minutes_text.setVisibility(View.VISIBLE);
            textView_center_text.setVisibility(View.INVISIBLE);
            textView_counter.setText(Long.toString(minutes_left));
            if (minutes_left == 1) {
                textView_minutes_text.setText(R.string.minute);
            } else {
                textView_minutes_text.setText(R.string.minutes);
            }
        }


    }


    // Helper method to show de difference between a [date , time] compared to Vasttrafik server [date , time]
    // We don't want to rely on that the local clock matches the servers, therefore we use the data that is always supplied from Vasttrafik
    private long vt_time_diff_minutes(String date_1, String time_1) {
        SimpleDateFormat input_format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date input_date = input_format.parse(date_1 + " " + time_1);
            Date vasttrafik_server_date = input_format.parse(journeyDetails.serverdate + " " + journeyDetails.servertime);

            long diffInMilliseconds = input_date.getTime() - vasttrafik_server_date.getTime();
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMilliseconds);

            return diffInMinutes;

        } catch (ParseException e) {
            return -1;
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) { // Save the state in case the fragment gets destroyed
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("onBus", onBus);
        savedInstanceState.putBoolean("arrived_at_destination", arrived_at_destination);
        savedInstanceState.putParcelable("journeyDetails_updated", journeyDetails);
    }

    // Called when a fragment is first attached to its context
    // Used to get reference to parent activity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parent = (MainActivity) getActivity();
    }

    @Override
    public void onDestroy() { // Kill the timer when closing fragment
        if (vt_update_timer != null) {
            vt_update_timer.cancel();
            vt_update_timer.purge();
        }
        if (ec_update_timer != null) {
            ec_update_timer.cancel();
            ec_update_timer.purge();
        }
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        // Find all the elements we need from th elayout
        TextView textview_line_short_name = (TextView) view.findViewById(R.id.info_line_name);
        TextView textView_source = (TextView) view.findViewById(R.id.info_source);
        TextView textView_destination = (TextView) view.findViewById(R.id.info_destination);
        textView_counter = (TextView) view.findViewById(R.id.timeTilArrival);
        textView_minutes_text = (TextView) view.findViewById(R.id.info_minutes_text);
        textView_above_circle = (TextView) view.findViewById(R.id.info_above_circle);
        textView_center_text = (TextView) view.findViewById(R.id.info_center_text);
        stop_circle = (Button) view.findViewById(R.id.info_stop);


        // Load the arguments from newInstance
        Bundle bundle = getArguments();
        source = bundle.getParcelable("source");
        destination = bundle.getParcelable("destination");
        departure_from_board = bundle.getParcelable("trip");
        journeyDetails = bundle.getParcelable("journey"); // Use this if no newer is stored in savedState


        // Set the names of the source and destination stops
        textView_source.setText(source.getNameWithoutCity());
        textView_destination.setText(destination.getNameWithoutCity());

        if (savedState != null) {
            Log.i("### info_frag", "has saved instance");
            onBus = savedState.getBoolean("onBus", false); // If has saved instance restore state. Otherwise assume we are not on the bus.
            arrived_at_destination = savedState.getBoolean("arrived_at_destination", false); // True if we already are at the destination
            journeyDetails = savedState.getParcelable("journeyDetails_updated");
        }

        textview_line_short_name.setText(departure_from_board.name);

        if (journeyDetails.color != null) {

            if (!journeyDetails.color.fgColor.equals("#ffffff")) { // Cannot use white on light background
                textview_line_short_name.setTextColor(Color.parseColor(journeyDetails.color.fgColor)); // Set the foreground color of the name text
            }
//
//            if (!journeyDetails.color.bgColor.equals("#ffffff")){ // White looks ugly as background when fragment background is gray
//                textview_line_short_name.setBackgroundColor(Color.parseColor(journeyDetails.color.bgColor));
//            }
        }

        // Create REST clients
        vt_client = new VTClient(this);
        ec_client = new ECClient(this);


        update_gui(); // Update GUI once since the timer will wait a defined amount of seconds until it starts

        if (!arrived_at_destination) { // has not arrived at destination yet.

            if (onBus) {
                show_stop_button();
            } else {
                hide_stop_button();
            }


            //  Create a timer that will update the status
            vt_update_timer = new Timer();
            vt_update_timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.i("### INFO", "TIMER EVENT, Checking Västtrafik API");

                    vt_client.get_journey_details(departure_from_board.journeyDetailRef);

                    //Check DetectBusService to see if we're on the bus
                    if (!got_bus_info_from_wifi) {
                        if (DetectBusService.onBus) {
                            AlarmService.setServiceAlarm(getActivity(), true, DetectBusService.dgwFound, destination.name);
                            onBus = true;
                            got_bus_info_from_wifi = true;
                            ec_update_timer = new Timer();
                            ec_update_timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Log.i("### INFO", "EC TIMER EVENT");
                                    Calendar hundred_seconds_old = Calendar.getInstance();
                                    hundred_seconds_old.add(Calendar.SECOND, -20);
                                    ec_client.get_bus_resource(DetectBusService.dgwFound, hundred_seconds_old.getTime(), Calendar.getInstance().getTime(), "Ericsson$Stop_Pressed_Value");
                                }
                            }, 0, EC_UPDATE_TIMER_INTERVAL);
                        }
                    }
                }
            }, 0, VT_UPDATE_TIMER_INTERVAL);

        } else {
            Log.i("### INFO", "has arrived at destination");

        }
        DetectBusService.setServiceAlarm(getActivity(), true, source.name, destination.name, departure_from_board.time, departure_from_board.name);

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
    public void got_sensor_data(List<Bus_info> bus_info) {
    }

    @Override
    public void got_sensor_data_from_all_buses(List<Bus_info> bus_info) {
    }

    @Override
    public void got_reource_data(List<Bus_info> bus_info) {
        if (bus_info == null) return;
        Bus_info b = bus_info.get(bus_info.size() - 1);
        Log.i("### SENSOR RESULT", "BUS ID:" + b.gatewayId + " RESOURCE:" + b.resourceSpec + " VALUE:" + b.value + " TIME:" + b.timestamp);
        stop_button_state(b.value.equals("true"));
    }

    @Override
    public void got_reource_data_from_all_buses(List<Bus_info> bus_info) {
    }

    @Override
    public void got_error(String during_method, String error_msg) {
        Log.i("### INFO ERR", error_msg);
    }
}
