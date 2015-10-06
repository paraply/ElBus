package se.elbus.oaakee.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;

import se.elbus.oaakee.R;


/**
 * Created by TH on 2015-10-05.
 */
public class PaymentFragment  extends Fragment {

    private Card mCard = new Card();
    private Punch mCurrentPunch;
    private TextView chargeView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_payment, container, false);
        chargeView = (TextView) v.findViewById(R.id.chargeText);
        updateCharge();
        return v;
    }

    private void updateCharge() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        chargeView.setText(formatter.format(mCard.getCharge()));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
