package se.elbus.oaakee.fragments;

import android.content.Context;
import android.location.Location;

public interface ITravelPresenter {
    void updateModelLocation(Context context);

    void warnGPSOff();

    void updateViewLocation(Location location);
}
