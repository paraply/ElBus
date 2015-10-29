package se.elbus.oaakee.fragments.presenter;

import android.content.Context;
import android.location.Location;
import android.widget.ArrayAdapter;

import java.util.List;

import se.elbus.oaakee.fragments.ITravelView;
import se.elbus.oaakee.fragments.model.ITravelModel;
import se.elbus.oaakee.fragments.model.TravelModelImpl;
import se.elbus.oaakee.restapi.vtmodel.Departure;

public class TravelPresenterImpl implements ITravelPresenter {
    private  final ITravelView mView;
    private final ITravelModel mModel;
    public TravelPresenterImpl(ITravelView travelView) {
        mView = travelView;
        mModel = new TravelModelImpl(this);
    }

    @Override
    public void updateModelLocation(Context context) {
        mModel.updateLocation(context);
    }

    @Override
    public void warnGPSOff() {
        mView.warnGPSOff();
    }

    @Override
    public void updateModelDepartures(String lineId) {
        mModel.updateDepartures(lineId);
    }

    @Override
    public void updateModelNearbyStops(Location location) {
        mModel.updateStops(location);
    }

    @Override
    public ArrayAdapter<List<Departure>> getDeparturesAdapter(Context context, ITravelView view) {
        return mModel.getDeparturesAdapter(context,view);
    }

    @Override
    public void updateViewDepartures() {
        mView.updateDepartures();
    }

    @Override
    public void updateViewNearbyStops() {
        mView.updateNearbyStops();
    }

    @Override
    public ArrayAdapter getBusStopsAdapter(Context context) {
        return mModel.getBusStopsAdapter(context);
    }

    @Override
    public void updateViewLocation(Location location) {
        mView.updateLocation(location);
    }
}
