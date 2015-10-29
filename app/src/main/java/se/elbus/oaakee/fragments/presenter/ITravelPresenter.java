package se.elbus.oaakee.fragments.presenter;

import android.content.Context;
import android.location.Location;

public interface ITravelPresenter {
    void updateModelLocation(Context context);
    void updateViewLocation(Location location);

    void warnGPSOff();

}
