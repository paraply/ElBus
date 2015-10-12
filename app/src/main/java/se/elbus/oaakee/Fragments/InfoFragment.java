package se.elbus.oaakee.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;

import se.elbus.oaakee.AlarmService;
import se.elbus.oaakee.R;

public class InfoFragment extends Fragment {

    private TextView mTV;
    private boolean onBus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    //For alarm functionality, add this
    //AlarmService.setServiceAlarm(getActivity(), true);
}
