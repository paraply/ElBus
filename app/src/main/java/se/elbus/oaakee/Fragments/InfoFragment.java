package se.elbus.oaakee.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import se.elbus.oaakee.AlarmService;
import se.elbus.oaakee.Buses.WifiFinder;
import se.elbus.oaakee.MainActivity;
import se.elbus.oaakee.R;
import se.elbus.oaakee.REST_API.EC_Callback;
import se.elbus.oaakee.REST_API.EC_Client;
import se.elbus.oaakee.REST_API.EC_Model.Bus_info;

public class InfoFragment extends Fragment implements EC_Callback{

    private TextView mTV;
    private boolean onBus;

    private MainActivity parent;
    private WifiFinder wifiFinder;
    private EC_Client ec_client; // TODO: Maybe will get reference from parent
    private String current_dgw;

    public InfoFragment(){}

    // To create a new instance of this fragment
    // TODO: These arguments will probably change...
    // TODO: Maybe use JourneyRefURL from VT_API...
    public static InfoFragment newInstance(String stop_ID, String line_ID, String destination_ID){
        InfoFragment infoFragment = new InfoFragment();

        Bundle fragment_args = new Bundle();                // Save the arguments in a bundle in case the state is destroyed and needs to be recreated (like screen rotation)
        fragment_args.putString("stop_ID", stop_ID);
        fragment_args.putString("line_ID", line_ID);
        fragment_args.putString("destination_ID", destination_ID);
        infoFragment.setArguments(fragment_args);

        return infoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ec_client = new EC_Client(this);

        wifiFinder = new WifiFinder(parent, "Electricity") {
            @Override
            // Found an Dgw close to us. We assume we are on this bus
            public void receiveDgw(String dgw) {
                current_dgw = dgw;
            }
        };

        super.onCreate(savedInstanceState);
    }

    // Called when a fragment is first attached to its context
    // Used to get reference to parent activity
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        parent = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info, container, false);

        onBus = false;

        Random ran = new Random();

        mTV = (TextView) v.findViewById(R.id.timeTilArrival);
        mTV.setText(Integer.toString(ran.nextInt(13) + 2));
        mTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vClick) {
                int minLeft = Integer.parseInt(mTV.getText().toString());
                if (minLeft <= 0) {
                    onBus = !onBus;
                    Random ran = new Random();
                    mTV.setText(Integer.toString(ran.nextInt(13) + 2));
                    modifyView();
                } else {
                    mTV.setText(Integer.toString(minLeft - 1));
                }
            }
        });

        return v;
    }

    private void modifyView() {
        View v = InfoFragment.this.getView();
        TextView tView = (TextView) v.findViewById(R.id.infoArrivesIn);
        TextView tBusName = (TextView) v.findViewById(R.id.infoBusName);
        if(onBus) {
            tView.setText("framme om");
        } else {
            Random ran = new Random();
            int busNr = ran.nextInt(55) + 1;
            tBusName.setText(busNr + ":an");
            tView.setText("ankommer om");
        }

    }

    @Override
    public void got_sensor_data(List<Bus_info> bus_info) {

    }

    @Override
    public void got_sensor_data_from_all_buses(List<Bus_info> bus_info) {

    }

    @Override
    public void got_reource_data(List<Bus_info> bus_info) {

    }

    @Override
    public void got_reource_data_from_all_buses(List<Bus_info> bus_info) {

    }

    @Override
    public void got_error(String during_method, String error_msg) {

    }

    //For alarm functionality, add this
    //AlarmService.setServiceAlarm(getActivity(), true);
}
