package se.elbus.oaakee.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import se.elbus.oaakee.R;
import se.elbus.oaakee.restapi.vtmodel.Departure;

/**
 * Custom adapter for departures ListView.
 */
public class DeparturesAdapter extends ArrayAdapter<List<Departure>> {
    private ITravelView mView;

    public DeparturesAdapter(Context context, List<List<Departure>> departures, ITravelView travelFragment) {
        super(context, R.layout.busline_row, departures);
        this.mView = travelFragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View v = layoutInflater.inflate(R.layout.busline_row, parent, false);

        List<Departure> departures = getItem(position);

        TextView lineNumber = (TextView) v.findViewById(R.id.busNumberTextView);

        if (departures.size() > 0) {
            lineNumber.setText(departures.get(0).sname);

            if (departures.get(0).sname.length() > 3) {
                lineNumber.setTextSize(18);
            }

            Drawable circle = lineNumber.getBackground();

            try {
                String backgroundColor = (departures.get(0)).fgColor;
                circle.setColorFilter(Color.parseColor(backgroundColor), PorterDuff.Mode.MULTIPLY);
            } catch (IllegalArgumentException e) {
                Log.e(TravelFragment.TAG, "Failed parsing color, set to a default colour");
                circle.setColorFilter(Color.parseColor("#CCCCCC"), PorterDuff.Mode.MULTIPLY);
            }
        }
        for (int i = 0; i < departures.size(); i++) {
            Departure departure = departures.get(i);
            String time;
            String date;
            boolean addDivider = true;

            if (i == departures.size() - 1) {
                addDivider = false; //last button does not need divider
            }

            if (departure.rtTime != null) {
                time = departure.rtTime;
                date = departure.rtDate;
            } else {
                time = departure.time;
                date = departure.date;
            }

            String minutesToDeparture;
            try {
                long minutes = getMinutesToDeparture(date, time);
                minutesToDeparture = "" + minutes;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                minutesToDeparture = "x"; //shows "x" if something goes wrong...
            }

            View busLineButtonView = createBusLineButton(v, layoutInflater, parent, departure.direction, minutesToDeparture, addDivider);
            setButtonClick(busLineButtonView, departure);
        }

        return v;
    }

    /**
     * Calculates minutes to departure from current time
     *
     * @param date Date in format "YYYY-MM-DD"
     * @param time Time in format "HH-MM"
     * @return difference from current time in minutes
     * @throws NumberFormatException If date or time is in wrong format.
     */
    private long getMinutesToDeparture(String date, String time) throws NumberFormatException {
        int year = Integer.valueOf(date.substring(0, 4));
        int month = Integer.valueOf(date.substring(5, 7));
        int day = Integer.valueOf(date.substring(8, 10));
        int hour = Integer.valueOf(time.substring(0, 2));
        int minute = Integer.valueOf(time.substring(3, 5));

        Calendar temp = Calendar.getInstance();
        temp.set(year, month - 1, day, hour, minute); //departure time. Month starts at 0

        Calendar today = Calendar.getInstance();

        long difference = temp.getTime().getTime() - today.getTime().getTime();
        difference /= 1000 * 60; //time in minutes

        return difference;
    }

    /**
     * Created button for bus line departure
     */
    private View createBusLineButton(View parentView, LayoutInflater layoutInflater, ViewGroup parent, String direction, String time, boolean addDivider) {
        View busLineButtonView = layoutInflater.inflate(R.layout.busline_button, parent, false);

        LinearLayout linearLayout = (LinearLayout) parentView.findViewById(R.id.busLineButtonLayout);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.addView(busLineButtonView, layoutParams);

        if (addDivider) {
            View listDivider = layoutInflater.inflate(R.layout.line_divider, parent, false);
            linearLayout.addView(listDivider, layoutParams);
        }

        setTextViewText(R.id.stationTextView, busLineButtonView, direction);

        if (time.equals("0")) {
            time = getContext().getString(R.string.now);
            TextView minTextView = (TextView) busLineButtonView.findViewById(R.id.minBelowTextView);
            minTextView.setVisibility(View.GONE);
        }

        setTextViewText(R.id.minutesTextView, busLineButtonView, time);

        return busLineButtonView;
    }

    /**
     * Sets text of TextView
     */
    private void setTextViewText(int id, View parent, String text) {
        TextView textView = (TextView) parent.findViewById(id);
        textView.setText(text);
    }

    /**
     * Sets button click
     */
    private void setButtonClick(final View view, final Departure departure) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mView.saveParcelable("trip", departure);
                mView.nextFragment();
            }
        });
    }

}
