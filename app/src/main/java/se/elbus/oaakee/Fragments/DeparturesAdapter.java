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

import org.w3c.dom.Text;

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
        Log.i("Travel", "--- DeparturesAdapter getView ---");
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View customView = layoutInflater.inflate(R.layout.busline_row, parent, false);

        List<Departure> departures = getItem(position);

        TextView lineNumber = (TextView)customView.findViewById(R.id.busNumberTextView);

        if (departures.size()>0) {
            lineNumber.setText(departures.get(0).sname);
        }

        for (Departure departure:departures){

            View busLineButtonView = createBusLineButton(customView, layoutInflater, parent, departure.direction, departure.time);
            setButtonClick(busLineButtonView);

        }

        return customView;
    }

    /**
     * Created button for bus line departure
     * @param parentView
     * @param layoutInflater
     * @param parent
     * @param direction
     * @param time
     * @return
     */
    private View createBusLineButton(View parentView, LayoutInflater layoutInflater, ViewGroup parent, String direction, String time){
        View busLineButtonView = layoutInflater.inflate(R.layout.busline_button,parent,false);

        LinearLayout linearLayout = (LinearLayout)parentView.findViewById(R.id.busLineButtonLayout);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.addView(busLineButtonView, layoutParams);

        setTextViewText(R.id.stationTextView,busLineButtonView,direction);
        setTextViewText(R.id.minutesTextView,busLineButtonView,time);

        return busLineButtonView;
    }

    /**
     * Sets text of TextView
     * @param id
     * @param parent
     * @param text
     */
    private void setTextViewText(int id, View parent, String text){
        TextView minutesText = (TextView)parent.findViewById(id);
        minutesText.setText(text);
    }

    /**
     * Sets button click
     * @param view
     */
    private void setButtonClick(final View view){
        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String toastMessage = "You clicked: " + ((TextView)view.findViewById(R.id.stationTextView)).getText();
                Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
