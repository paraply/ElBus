package se.elbus.oaakee.fragments.presenter;

import android.content.Context;
import android.location.Location;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.List;

import se.elbus.oaakee.fragments.ITravelView;
import se.elbus.oaakee.restapi.vtmodel.Departure;

public interface ITravelPresenter {
    void updateModelLocation(Context context);
    void updateViewLocation(Location location);

    void warnGPSOff();

    void updateModelDepartures(String lineId);

    void updateModelNearbyStops(Location location);

    ArrayAdapter<List<Departure>> getDeparturesAdapter(Context context, ITravelView view);

    void updateViewDepartures();

    void updateViewNearbyStops();

    ArrayAdapter getBusStopsAdapter(Context context);
}
