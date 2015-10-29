package se.elbus.oaakee.fragments;

import android.content.Context;
import android.location.Location;

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
    public void updateViewLocation(Location location) {
        mView.updateLocation(location);
    }
}
