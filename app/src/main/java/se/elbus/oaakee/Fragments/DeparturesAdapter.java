package se.elbus.oaakee.Fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import se.elbus.oaakee.R;

/**
 * Custom adapter for departures ListView.
 *
 * TODO: should extend "ArrayAdapter<BusLine>" or something similar instead of "<String>"
 *
 * Created by Tobias on 15-09-27.
 */
public class DeparturesAdapter extends ArrayAdapter<String> {
    public DeparturesAdapter(Context context, String[] string) {
        super(context, R.layout.busline_row,string);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View customView = layoutInflater.inflate(R.layout.busline_row, parent, false);

        String busDirection = getItem(position);

        final Button topButton = (Button) customView.findViewById(R.id.topDepartureButton);
        final Button bottomButton = (Button) customView.findViewById(R.id.bottomDepartureButton);

        topButton.setText(busDirection);
        bottomButton.setText(busDirection + " 2");

        setButtonAction(topButton);
        setButtonAction(bottomButton);

        return customView;
    }

    private void setButtonAction(final Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toastMessage = "You clicked: " + button.getText();
                Toast.makeText(getContext(),toastMessage,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
