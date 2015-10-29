package se.elbus.oaakee.fragments.model;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import se.elbus.oaakee.R;
import se.elbus.oaakee.fragments.DeparturesAdapter;
import se.elbus.oaakee.fragments.ITravelView;
import se.elbus.oaakee.fragments.presenter.ITravelPresenter;
import se.elbus.oaakee.restapi.VTCallback;
import se.elbus.oaakee.restapi.VTClient;
import se.elbus.oaakee.restapi.vtmodel.Departure;
import se.elbus.oaakee.restapi.vtmodel.DepartureBoard;
import se.elbus.oaakee.restapi.vtmodel.JourneyDetail;
import se.elbus.oaakee.restapi.vtmodel.LocationList;
import se.elbus.oaakee.restapi.vtmodel.StopLocation;

public class TravelModelImpl implements ITravelModel, LocationListener, VTCallback {
    private final long LATEST_LOCATION_TIME_MILLIS = 1 * 60 * 1000;
    private final int LOCATION_ACCURACY = Criteria.ACCURACY_LOW; // "For horizontal and vertical position this corresponds roughly to an accuracy of greater than 500 meters."
    private final VTClient mVTClient;

    double mSimulatorLongitude = 11.972305;
    double mSimulatorLatitude = 57.707792;

    private final List<List<Departure>> mDeparturesSorted = new ArrayList<>();
    private final List<String> mBusStops = new ArrayList<>();


    private final ITravelPresenter mPresenter;
    public TravelModelImpl(ITravelPresenter travelPresenter) {
        mPresenter = travelPresenter;
        mVTClient = new VTClient(this);
    }

    @Override
    public void updateLocation(Context context) throws SecurityException {
        Criteria locationCriteria = new Criteria();
        locationCriteria.setAccuracy(LOCATION_ACCURACY);

        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        String bestProvider = manager.getBestProvider(locationCriteria, false);
        Location fastLocation;
        if (Build.FINGERPRINT.startsWith("generic")) {
            fastLocation = new Location(bestProvider);
            fastLocation.setLatitude(mSimulatorLatitude);
            fastLocation.setLongitude(mSimulatorLongitude);
            fastLocation.setTime(System.currentTimeMillis());
        } else {
            fastLocation = manager.getLastKnownLocation(bestProvider);
        }

        /*
         If a long time has passed since the last scan.
         */
        if (fastLocation == null || fastLocation.getTime() + LATEST_LOCATION_TIME_MILLIS < System.currentTimeMillis()) {
            if (!manager.isProviderEnabled(bestProvider)) {
                mPresenter.warnGPSOff();
            }
            manager.requestSingleUpdate(locationCriteria, this, Looper.myLooper());
        } else {
            onLocationChanged(fastLocation);
        }

    }

    @Override
    public void updateDepartures(String lineId) {
        mVTClient.getDepartureBoard(lineId);
    }

    @Override
    public void updateStops(Location location) {
        mVTClient.getNearbyStops(location.getLatitude() + "", location.getLongitude() + "", "30", "1000");
    }

    @Override
    public ArrayAdapter<List<Departure>> getDeparturesAdapter(Context context, ITravelView view) {
        return new DeparturesAdapter(context, mDeparturesSorted, view);
    }

    @Override
    public ArrayAdapter getBusStopsAdapter(Context context) {
        return new ArrayAdapter<>(context, R.layout.spinner_item, mBusStops);
    }

    @Override
    public void onLocationChanged(Location location) {
        mPresenter.updateViewLocation(location);
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    @Override
    public void onProviderEnabled(String provider) {

    }
    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void handleJourneyDetails(JourneyDetail journeyDetail) {

    }
    @Override
    public void handleNearbyStops(LocationList locationList) {
        List<StopLocation> stops1 = new ArrayList<>();
        boolean contains;

        for (StopLocation s : locationList.stoplocation) {
            String stopName = s.name;

            contains = false;

            for (StopLocation d : stops1) {
                if ((stopName.equals(d.name))) {
                    contains = true;
                }
            }

            if (!contains) {
                stops1.add(s);
            }
        }



        mBusStops.clear();
        for (StopLocation s : stops1) {
            mBusStops.add(s.getNameWithoutCity());
        }
        mPresenter.updateViewNearbyStops();
    }
    @Override
    public void handleDepartureBoard(DepartureBoard board) {
        List<Departure> allDepartures = board.departure;
        mDeparturesSorted.clear();

        List<String> shortName = new ArrayList<>(); //list of unique short name sorted by departure time

        for (Departure departure : allDepartures) {
            if (!(shortName.contains(departure.sname))) {
                shortName.add(departure.sname);
            }
        }

        for (String s : shortName) {
            List<Departure> departures = new ArrayList<>();

            for (Departure departure : allDepartures) { //loop through all departures
                if (departure.sname.equals(s)) {
                    boolean alreadyInList = false;

                    for (Departure d2 : departures) { //Checks if direction of this departure is already in list
                        if (departure.direction.equals(d2.direction)) {
                            alreadyInList = true;
                            break;
                        }
                    }

                    if (!alreadyInList) {
                        departures.add(departure);
                        //Log.d(TAG,"Added: " + departure.sname + ", Dir: " + departure.direction + ", time: " + departure.time);
                    }
                }
            }
            mDeparturesSorted.add(departures); //Adds list of departures for one single bus line number
        }
        mPresenter.updateViewDepartures();
    }
    @Override
    public void handleError(String during_method, String error_msg) {
    }
}
