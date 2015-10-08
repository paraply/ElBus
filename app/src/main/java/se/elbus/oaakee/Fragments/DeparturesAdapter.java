package se.elbus.oaakee.Fragments;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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

        final Button topButton = (Button) customView.findViewById(R.id.topDepartureButton);
        final Button bottomButton = (Button) customView.findViewById(R.id.bottomDepartureButton);

        Button thirdButton = new Button(getContext());

        Log.i("Travel","departure size: " + departures.size());

        switch (departures.size()){
            case 1:
                topButton.setText(departures.get(0).name);
                break;
            case 2:
                topButton.setText(departures.get(0).name);
                bottomButton.setText(departures.get(1).name);
                break;
            case 3:
                topButton.setText(departures.get(0).name);
                bottomButton.setText(departures.get(1).name);
                thirdButton.setText(departures.get(2).name);
                break;
            case 4:
                topButton.setText("4");
                bottomButton.setText("4");
                thirdButton.setText("4");
                break;
            default:
                topButton.setText("size " + departures.size());
                bottomButton.setText("default");
                thirdButton.setText("default");
                break;
        }

        LinearLayout ll = (LinearLayout)customView.findViewById(R.id.busLineButtonLayout);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.addView(thirdButton, lp);



        setButtonAction(topButton);
        setButtonAction(bottomButton);
        setButtonAction(thirdButton);

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
