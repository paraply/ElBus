package se.elbus.oaakee.Fragments;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import se.elbus.oaakee.R;
import se.elbus.oaakee.REST_API.VT_Model.Departure;

/**
 * Custom adapter for departures ListView.
 *
 * Created by Tobias on 15-09-27.
 */
public class DeparturesAdapter extends ArrayAdapter<List<Departure>> {
    public DeparturesAdapter(Context context, List<List<Departure>> departures) {
        super(context, R.layout.busline_row, departures);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("Travel","--- DeparturesAdapter getView ---");
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View customView = layoutInflater.inflate(R.layout.busline_row, parent, false);

        List<Departure> departures = getItem(position);


        TextView lineNumber = (TextView)customView.findViewById(R.id.busNumberTextView);
        if (departures.size()>0) {
            lineNumber.setText(departures.get(0).sname);
        }
        //final Button topButton = (Button) customView.findViewById(R.id.topDepartureButton);

        for (Departure departure:departures){

            Button button = new Button(getContext());

            LinearLayout linearLayout = (LinearLayout)customView.findViewById(R.id.busLineButtonLayout);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            linearLayout.addView(button, layoutParams);
            button.setGravity(Gravity.LEFT);

            button.setText(departure.direction + " - " + departure.time);
            setButtonAction(button);
        }





        //setButtonAction(topButton);

        return customView;
    }

    private void setButtonAction(final Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toastMessage = "You clicked: " + button.getText();
                Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
