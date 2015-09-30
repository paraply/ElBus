package se.elbus.oaakee;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment for the "Choose destination"-layout
 *
 * Created by Tobias on 15-09-30.
 */
public class DestinationFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View destinationView =  inflater.inflate(R.layout.destination_chooser, container, false);

        //fixa lista på destinations osv här


        return destinationView;
    }
}
