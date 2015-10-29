package se.elbus.oaakee.fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import se.elbus.oaakee.R;
import se.elbus.oaakee.fragments.presenter.ITravelPresenter;
import se.elbus.oaakee.fragments.presenter.TravelPresenterImpl;
import se.elbus.oaakee.restapi.vtmodel.Departure;
import se.elbus.oaakee.restapi.vtmodel.StopLocation;

public class TravelFragment extends Fragment implements AdapterView.OnItemSelectedListener, ITravelView {

    protected static final String TAG = "Travel";

    private Spinner mBusStops;

    private FragmentSwitchCallbacks mFragmentSwitcher;
    private Bundle mSavedState;

    private ArrayAdapter mDepartureListAdapter;
    ArrayAdapter<List<Departure>> mDeparturesAdapter;

    private ITravelPresenter mPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFragmentSwitcher = (FragmentSwitchCallbacks) context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSavedState = new Bundle();
        mPresenter = new TravelPresenterImpl(this);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_travel, container, false); // Main view.

        mDepartureListAdapter = mPresenter.getBusStopsAdapter(getContext());
        mDepartureListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBusStops = (Spinner) v.findViewById(R.id.busStopSpinner);
        mBusStops.setOnItemSelectedListener(this);
        mBusStops.setAdapter(mDepartureListAdapter);

        mDeparturesAdapter = mPresenter.getDeparturesAdapter(getContext(), this);
        ListView mDeparturesList = (ListView) v.findViewById(R.id.departuresListView);
        mDeparturesList.setAdapter(mDeparturesAdapter);

        return v;
    }
    @Override
    public void onStart() {
        super.onStart();
        mPresenter.updateModelLocation(getContext());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "Spinner clicked: " + mBusStops.getSelectedItem().toString() + ", Position: " + position);
        mPresenter.updateModelBusStop(position);
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.i(TAG, "Spinner nothing selected");
    }

    @Override
    public void saveParcelable(String key, Parcelable parcelable) {
        mSavedState.putParcelable(key,parcelable);
    }

    @Override
    public void nextFragment() {
        mFragmentSwitcher.nextFragment(mSavedState);
    }

    @Override
    public void warnGPSOff() {
        Toast.makeText(getContext(), R.string.gps_off_warning_text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateLocation(Location location) {
        mPresenter.updateModelNearbyStops(location);
    }

    @Override
    public void updateDepartures() {
        mDeparturesAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateNearbyStops() {
        mDepartureListAdapter.notifyDataSetChanged();
    }
}
