package se.elbus.oaakee;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class TravelFragment extends Fragment {

    private Boolean mMaybe;
    private TextView mHelloText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mMaybe = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_travel,container,false);

        mHelloText = (TextView) v.findViewById(R.id.hello_text_view);
        mHelloText.setText(getText(R.string.hello_world));

        return v;
    }
}
