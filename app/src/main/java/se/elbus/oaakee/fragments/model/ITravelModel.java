package se.elbus.oaakee.fragments.model;

import android.content.Context;
import android.location.Location;
import android.widget.ArrayAdapter;

import java.util.List;

import se.elbus.oaakee.fragments.ITravelView;
import se.elbus.oaakee.restapi.vtmodel.Departure;
import se.elbus.oaakee.restapi.vtmodel.StopLocation;

public interface ITravelModel {
    void updateLocation(Context context);

    void updateDepartures(String lineId);

    void updateStops(Location location);

    ArrayAdapter<List<Departure>> getDeparturesAdapter(Context context, ITravelView view);


    ArrayAdapter getBusStopsAdapter(Context context);

    StopLocation getStop(int position);
}
