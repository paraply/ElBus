package se.elbus.oaakee.fragments;

import android.location.Location;
import android.os.Parcelable;

public interface ITravelView {
    void saveParcelable(String tag, Parcelable parcelable);

    void nextFragment();

    void warnGPSOff();

    void updateLocation(Location location);

    void updateDepartures();

    void updateNearbyStops();
}
