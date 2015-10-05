package se.elbus.oaakee.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import se.elbus.oaakee.R;

/**
 * Fragment for the "Choose destination"-layout
 *
 * Created by Tobias on 15-09-30.
 */
public class DestinationFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View destinationView =  inflater.inflate(R.layout.destination_chooser, container, false);

        populateDestinationsList(destinationView);

        return destinationView;
    }

    /**
     * Populates destination list
     *
     * @param destinationView The choose destination view
     */
    private void populateDestinationsList(View destinationView){
        String destinations[] = {"Kapellplatsen", "Götaplatsen", "Valand", "Kungsportsplatsen", "Domkyrkan", "Lilla Bommen"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,destinations);
        ListView destinationsListView = (ListView) destinationView.findViewById(R.id.destinationsListView);
        destinationsListView.setAdapter(adapter);
    }
}
