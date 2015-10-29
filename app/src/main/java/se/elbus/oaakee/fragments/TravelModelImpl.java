package se.elbus.oaakee.fragments;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;

public class TravelModelImpl implements ITravelModel, LocationListener {
    private final long LATEST_LOCATION_TIME_MILLIS = 1 * 60 * 1000;
    private final int LOCATION_ACCURACY = Criteria.ACCURACY_LOW; // "For horizontal and vertical position this corresponds roughly to an accuracy of greater than 500 meters."

    double mSimulatorLongitude = 11.972305;
    double mSimulatorLatitude = 57.707792;


    private final ITravelPresenter mPresenter;
    public TravelModelImpl(ITravelPresenter travelPresenter) {
        mPresenter = travelPresenter;
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
}
