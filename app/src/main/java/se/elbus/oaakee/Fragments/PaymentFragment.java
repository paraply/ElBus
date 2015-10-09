package se.elbus.oaakee.Fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.NumberFormat;

import se.elbus.oaakee.R;


/**
 * Created by TH on 2015-10-05.
 */
public class PaymentFragment  extends Fragment {

    private Card mCard = new Card();
    private Ticket mCurrentTicket;
    private Button mTicketButton;
    private ProgressBar mProgressbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_payment, container, false);

        /*
        Finds the view and updates the amount of currency.
         */
        final TextView mCurrencyView = (TextView) v.findViewById(R.id.chargeText);
        updateCharge(mCurrencyView);

        /*
        Finds the progress bar.
         */
        mProgressbar = (ProgressBar) v.findViewById(R.id.countdown_progressbar);

        /*
        Finds the button and binds a listener to it.
         */
        mTicketButton = (Button) v.findViewById(R.id.button_get_ticket);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentTicket = getTicket();
                hasTicket();
            }
        };
        mTicketButton.setOnClickListener(listener);


        return v;
    }

    private void hasTicket() {
        mTicketButton.setVisibility(View.INVISIBLE);

        long time = (mCurrentTicket.mValidTo - System.currentTimeMillis());

        if (time < 0){
            hasNotTicket();
            return;
        }

        CountDownTimer timer = new CountDownTimer(time,time/mProgressbar.getMax()) {
            @Override
            public void onTick(long millisUntilFinished) {
                mProgressbar.incrementProgressBy(-1);
            }

            @Override
            public void onFinish() {
                hasNotTicket();
            }
        };
        timer.start();
    }

    private void hasNotTicket() {
mProgressbar.setProgress(mProgressbar.getMax());
        mCurrentTicket = null;

    }

    private Ticket getTicket() {
        return new Ticket();
    }

    private void updateCharge(TextView v) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        v.setText(formatter.format(mCard.getCharge()));
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
