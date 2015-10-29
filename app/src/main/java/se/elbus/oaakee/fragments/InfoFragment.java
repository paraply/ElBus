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

import se.elbus.oaakee.MainActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import se.elbus.oaakee.R;
import se.elbus.oaakee.buses.Buses;
import se.elbus.oaakee.buses.WifiFinder;
import se.elbus.oaakee.restapi.ECCallback;
import se.elbus.oaakee.restapi.ECClient;
import se.elbus.oaakee.restapi.VTCallback;
import se.elbus.oaakee.restapi.VTClient;
import se.elbus.oaakee.restapi.ecmodel.busInfo;
import se.elbus.oaakee.restapi.vtmodel.Departure;
import se.elbus.oaakee.restapi.vtmodel.DepartureBoard;
import se.elbus.oaakee.restapi.vtmodel.JourneyDetail;
import se.elbus.oaakee.restapi.vtmodel.LocationList;
import se.elbus.oaakee.restapi.vtmodel.Stop;
import se.elbus.oaakee.restapi.vtmodel.StopLocation;
import se.elbus.oaakee.services.AlarmService;
import se.elbus.oaakee.services.DetectBusService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class InfoFragment extends Fragment implements VTCallback, ECCallback {

    private static final int VT_UPDATE_TIMER_INTERVAL = 20000; // Update Västtrafik every 20000 ms
    private static final int EC_UPDATE_TIMER_INTERVAL = 10000; // Update Electricity every 10000 ms
    private boolean onBus;
    private String dgwFound;
    private Timer ec_update_timer;
    private boolean mOnBus;
    private boolean mHasWifiInfo;
    private boolean mAtDestination;
    private Context mContext;
    private VTClient mVTClient;
    private ECClient mECClient;
    private TextView mTxtFinishedIn;
    private TextView mTxtTimeLeft;
    private TextView mTxtMin;
    private TextView mTxtCenter;
    private Button mBtnStop;
    private Timer mVtTimer;
    private Timer mEcTimer;


    private StopLocation mSource;
    private Stop mDestination;
    private Departure mDeparture;
    private JourneyDetail mJourney;

    private boolean mUseSourceTimetable, mUseDestinationTimetable;


    private void hideStopBtn() {
        mBtnStop.setVisibility(View.INVISIBLE);
    }

    private void showStopBtn() {
        mBtnStop.setVisibility(View.VISIBLE);
    }

    private void setStopBtnPressed(boolean pressed) {
        mBtnStop.setPressed(pressed);
    }

    // Update the GUI to show TIME and STOP information
    private void updateGui() {
        Stop journey_source = null, journey_destination = null;

        if (mJourney == null) {
            Toast.makeText(mContext, "Error: No details of this journey", Toast.LENGTH_LONG).show();
            return;
        }

        // Compare names from source stop and destination stop
        // with the results from journeyDetails
        for (Stop s : mJourney.stop) {
            if (s.name.equals(mSource.name)) { // Found a match with source stop and a journey details stop
                journey_source = s;
                Log.i("### INFO", "Found src match stop");
            } else if (s.name.equals(mDestination.name)) { // Found a match with destination stop and a journey details stop
                journey_destination = s;
                Log.i("### INFO", "Found dst match stop");
            }
        }


        if (!mOnBus) { // We are not on the bus yet according to Västtrafik OR ElectriCity WiFi-finder
            // ***** check if on bus OR get SOURCE time
            if (journey_source != null) {
                Log.i("### INFO SRC", journey_source.name + " RT ARRIVAL TIME: " + journey_source.rtArrTime + " TIMETABLE: " + journey_source.arrTime);


                if (journey_source.rtArrTime == null) { // If bus has arrived at source || no real time data is available

                    Log.i("### INFO SRC", "NO RT, checking timetable diff: " + vtTimeDiff(journey_source.arrDate, journey_source.arrTime));
                    if (vtTimeDiff(journey_source.arrDate, journey_source.arrTime) <= 0) { // Check timetable so we know if real time data is unavailable  if we really are on the bus
                        mOnBus = true;
                        mUseSourceTimetable = false;
                    } else { // Real time data is unavailable for source
                        mUseSourceTimetable = true;
                    }

                }
            } else {
                Log.e("### INFO", "BAD SOURCE, NO STOP NAME MATCHED");
                Toast.makeText(mContext, "Error: Cannot find source stop on this journey", Toast.LENGTH_LONG).show();
                return;
            }
        }

        // ***** check if arrived at our stop OR get DESTINATION time
        if (journey_destination != null) {
            Log.i("### INFO DEST", mDestination.name + " RT ARRIVAL TIME " + journey_destination.rtArrTime + " TIMETABLE: " + journey_destination.arrTime);
            if (journey_destination.rtArrTime == null) {

                Log.i("### INFO DEST", "NO RT, checking timetable diff: " + vtTimeDiff(journey_destination.arrDate, journey_destination.arrTime));
                if (vtTimeDiff(journey_destination.arrDate, journey_destination.arrTime) <= 0) { // Check timetable so we know if real time data is unavailable if we really have arrived at the destination
                    mAtDestination = true;
                    mUseDestinationTimetable = false;
                } else { // Real time data is unavailable for destination
                    mUseDestinationTimetable = true;
                }
            }
        } else {
            Log.e("### INFO", "BAD DESTINATION, NO STOP NAME MATCHED");
            Toast.makeText(mContext, "Error: Cannot find destination stop on this journey", Toast.LENGTH_LONG).show();
            return;
        }


        if (mAtDestination) { // We are at the destination. Update the GUI to show the user.
            hideStopBtn();
            mTxtFinishedIn.setVisibility(View.INVISIBLE);
            if (mVtTimer != null) {
                mVtTimer.cancel(); // Stop the timer since we don't need it no more
            }
            if (mEcTimer != null) {
                mEcTimer.cancel();
            }
            mTxtTimeLeft.setVisibility(View.INVISIBLE);
            mTxtMin.setVisibility(View.INVISIBLE);
            mTxtCenter.setVisibility(View.VISIBLE);
            mTxtCenter.setText(R.string.arrived);
            mTxtCenter.setTextSize(45);

        } else {


            long minutes_left;

            if (mOnBus) { // We are on the bus. Show time until we arrive at the destination
                showStopBtn();
                mTxtFinishedIn.setText(R.string.arrive_at_destination); // "Arrive at destination" string

                minutes_left = mUseDestinationTimetable ? vtTimeDiff(journey_destination.arrDate, journey_destination.arrTime) :
                        vtTimeDiff(journey_destination.rtArrDate, journey_destination.rtArrTime);


            } else { // We are not on the bus yet. Show time until it arrives to our stop
                hideStopBtn();

                minutes_left = mUseSourceTimetable ? vtTimeDiff(journey_source.depDate, journey_source.depTime) :
                        vtTimeDiff(journey_source.rtDepDate, journey_source.rtDepTime);

            }

            showMinutes(minutes_left);

        }
    }

    // Show the counter e.g "1 minute" or "2 minutes" and "departures" or "arrives" above the circle
    private void showMinutes(long minutes_left) {
        if (minutes_left <= 0) {
            if (!mOnBus) {
                mTxtFinishedIn.setText(R.string.departures); // "departures" string
            } else {
                mTxtFinishedIn.setText(R.string.arrives); // "arrives" string
            }
            mTxtTimeLeft.setVisibility(View.INVISIBLE);
            mTxtMin.setVisibility(View.INVISIBLE);
            mTxtCenter.setVisibility(View.VISIBLE);
            mTxtCenter.setText(R.string.now);
            mTxtCenter.setTextSize(70);
        } else {
            if (!mOnBus) {
                mTxtFinishedIn.setText(R.string.departures_in); // "departures in" string
            } else {
                mTxtFinishedIn.setText(R.string.arrives_in); // "arrives in" string
            }
            mTxtTimeLeft.setVisibility(View.VISIBLE);
            mTxtMin.setVisibility(View.VISIBLE);
            mTxtCenter.setVisibility(View.INVISIBLE);
            mTxtTimeLeft.setText(Long.toString(minutes_left));
            if (minutes_left == 1) {
                mTxtMin.setText(R.string.minute);
            } else {
                mTxtMin.setText(R.string.minutes);
            }
        }


    }

    /**
<<<<<<< HEAD
     * Helper method to show de difference between a [date , time] compared to Vasttrafik server
     * [date , time] We don't want to rely on that the local clock matches the servers, therefore we
     * use the data that is always supplied from Vasttrafik
=======
     * Helper method to show de difference between a [date , time] compared to Vasttrafik server [date , time]
     * We don't want to rely on that the local clock matches the servers, therefore we use the data that is always supplied from Vasttrafik
>>>>>>> 6b3d6ae644c1e4cff0ad4ea76256e5c12e96226a
     *
     * @return the time difference in minutes.
     */
    private long vtTimeDiff(String date_1, String time_1) {
        SimpleDateFormat input_format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date input_date = input_format.parse(date_1 + " " + time_1);
            Date vasttrafik_server_date = input_format.parse(mJourney.serverdate + " " + mJourney.servertime);

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
        savedInstanceState.putBoolean("mOnBus", mOnBus);
        savedInstanceState.putBoolean("mAtDestination", mAtDestination);
        savedInstanceState.putParcelable("journeyDetails_updated", mJourney);
    }

    // Called when a fragment is first attached to its context
    // Used to get reference to mContext activity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getContext();
    }

    @Override
    public void onDestroy() { // Kill the timer when closing fragment
        if (mVtTimer != null) {
            mVtTimer.cancel();
            mVtTimer.purge();
        }
        if (mEcTimer != null) {
            mEcTimer.cancel();
            mEcTimer.purge();
        }
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        // Find all the elements we need from the layout
        TextView textview_line_short_name = (TextView) view.findViewById(R.id.info_line_name);
        TextView textView_source = (TextView) view.findViewById(R.id.info_source);
        TextView textView_destination = (TextView) view.findViewById(R.id.info_destination);
        mTxtTimeLeft = (TextView) view.findViewById(R.id.timeTilArrival);
        mTxtMin = (TextView) view.findViewById(R.id.info_minutes_text);
        mTxtFinishedIn = (TextView) view.findViewById(R.id.info_above_circle);
        mTxtCenter = (TextView) view.findViewById(R.id.info_center_text);
        mBtnStop = (Button) view.findViewById(R.id.info_stop);


        // Load the arguments from newInstance
        Bundle bundle = getArguments();
        mSource = bundle.getParcelable("source");
        mDestination = bundle.getParcelable("destination");
        mDeparture = bundle.getParcelable("trip");
        mJourney = bundle.getParcelable("journey"); // Use this if no newer is stored in savedState


        // Set the names of the source and destination stops
        textView_source.setText(mSource.getNameWithoutCity());
        textView_destination.setText(mDestination.getNameWithoutCity());

        if (savedState != null) {
            Log.i("### info_frag", "has saved instance");
            mOnBus = savedState.getBoolean("mOnBus", false); // If has saved instance restore state. Otherwise assume we are not on the bus.
            mAtDestination = savedState.getBoolean("mAtDestination", false); // True if we already are at the destination
            mJourney = savedState.getParcelable("journeyDetails_updated");
        }

        textview_line_short_name.setText(mDeparture.name);

        if (mJourney.color != null) {

            if (!mJourney.color.fgColor.equals("#ffffff")) { // Cannot use white on light background
                textview_line_short_name.setTextColor(Color.parseColor(mJourney.color.fgColor)); // Set the foreground color of the name text
            }
//
//            if (!journeyDetails.color.bgColor.equals("#ffffff")){ // White looks ugly as background when fragment background is gray
//                textview_line_short_name.setBackgroundColor(Color.parseColor(journeyDetails.color.bgColor));
//            }
        }

        // Create REST clients
        mVTClient = new VTClient(this);
        mECClient = new ECClient(this);


        updateGui(); // Update GUI once since the timer will wait a defined amount of seconds until it starts

        if (!mAtDestination) { // has not arrived at destination yet.

            if (mOnBus) {
                showStopBtn();
            } else {
                hideStopBtn();
            }


            //  Create a timer that will update the status
            mVtTimer = new Timer();
            mVtTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.i("### INFO", "TIMER EVENT, Checking Västtrafik API");

                    mVTClient.getJourneyDetails(mDeparture.journeyDetailRef);

                    //Check DetectBusService to see if we're on the bus
                    if (!mHasWifiInfo) {
                        if (DetectBusService.onBus) {
                            AlarmService.setServiceAlarm(getActivity(), true, DetectBusService.dgwFound, mDestination.name);
                            mOnBus = true;
                            mHasWifiInfo = true;
                            mEcTimer = new Timer();
                            mEcTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Log.i("### INFO", "EC TIMER EVENT");
                                    Calendar hundred_seconds_old = Calendar.getInstance();
                                    hundred_seconds_old.add(Calendar.SECOND, -20);
                                    mECClient.getBusResource(DetectBusService.dgwFound, hundred_seconds_old.getTime(), Calendar.getInstance().getTime(), "Ericsson$Stop_Pressed_Value");
                                }
                            }, 0, EC_UPDATE_TIMER_INTERVAL);
                        }
                    }
                }
            }, 0, VT_UPDATE_TIMER_INTERVAL);

        } else {
            Log.i("### INFO", "has arrived at destination");
        }

        Log.i("### WIFI", "Checking wifi now");
        //Check wififinder for connection
        Buses.initBuses(getActivity());
        WifiFinder wifiFinder = new WifiFinder(getActivity(), "ElBus") {
            @Override
            // Found an Dgw close to us. We assume we are on this bus
            public void receiveDgw(String dgw) {
                Log.i("### INFO", "Found Wifi, dgw: " + dgw);
                /**
                 *  Should also check if bus we think we're on matches the choice made.
                 *  Does not matter for prototype since we only target line 55.
                 */
                if (!onBus) {
                    AlarmService.setServiceAlarm(getActivity(), true, dgw, mDestination.name);
                    onBus = true;
                    dgwFound = dgw;
                    ec_update_timer = new Timer();
                    ec_update_timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Log.i("### INFO", "EC TIMER EVENT");
                            Calendar hundred_seconds_old = Calendar.getInstance();
                            hundred_seconds_old.add(Calendar.SECOND, -20);
                            mECClient.getBusResource(dgwFound, hundred_seconds_old.getTime(), Calendar.getInstance().getTime(), "Ericsson$Stop_Pressed_Value");
                        }
                    }, 0, EC_UPDATE_TIMER_INTERVAL);
                }
            }
        };

        //DetectBusService.setServiceAlarm(getActivity(), true, source.name, destination.name, departure_from_board.time, departure_from_board.name);
        DetectBusService.setServiceAlarm(getActivity(), true, mSource.name, mDestination.name, mDeparture.time, mDeparture.name);

        return view;
    }


    @Override
    public void handleJourneyDetails(JourneyDetail journeyDetail) {
        mJourney = journeyDetail;
        Log.i("### INFO", "Got new journeydetails. Updating GUI. ServerTIME: " + journeyDetail.serverdate + " " + journeyDetail.servertime);
        updateGui();
    }

    @Override
    public void handleNearbyStops(LocationList locationList) {
    }

    @Override
    public void handleDepartureBoard(DepartureBoard departureBoard) {
    }

    @Override
    public void handleSensorData(List<busInfo> busInfo) {
    }

    @Override
    public void handleSensorDataFromAllBuses(List<busInfo> busInfo) {
    }

    @Override
    public void handleResourceData(List<busInfo> busInfo) {
        if (busInfo == null) return;
        busInfo b = busInfo.get(busInfo.size() - 1);
        Log.i("### SENSOR RESULT", "BUS ID:" + b.gatewayId + " RESOURCE:" + b.resourceSpec + " VALUE:" + b.value + " TIME:" + b.timestamp);
        setStopBtnPressed(b.value.equals("true"));
    }

    @Override
    public void handleResourceDataFromAllBuses(List<busInfo> busInfo) {
    }

    @Override
    public void handleError(String during_method, String error_msg) {
        Log.i("### INFO ERR", error_msg);
    }
}
