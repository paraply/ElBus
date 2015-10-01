package se.elbus.oaakee;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class InfoFragment extends Fragment {

    private TextView mTV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info, container, false);


        mTV = (TextView) v.findViewById(R.id.timeTilArrival);
        mTV.setText("17");
        mTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vClick) {
                if (mTV.getText().equals("17")) {
                    mTV.setText("7");
                } else {
                    mTV.setText("17");
                }
            }
        });

        return v;
    }
}
