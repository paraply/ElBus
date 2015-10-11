package se.elbus.oaakee.Fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import se.elbus.oaakee.R;


/**
 * Created by TH on 2015-10-05.
 */
public class PaymentFragment  extends Fragment {

    private Card mCard = new Card();
    private Ticket mCurrentTicket;
    private Button mTicketButton;
    private ProgressBar mProgressbar;
    private TextView mTimeLeftText;
    private LinearLayout mTimeLeftViews;
    private TextView mCurrencyView;
    private TextView mHistoryView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_payment, container, false);

        /*
        Finds the money view and updates the amount of currency.
         */
        mCurrencyView = (TextView) v.findViewById(R.id.chargeText);
        updateCharge();

        /*
        Finds the progress bar.
         */
        mProgressbar = (ProgressBar) v.findViewById(R.id.countdown_progressbar);

        /*
        Finds the countdown text.
         */
        mTimeLeftText = (TextView) v.findViewById(R.id.timeleft);
        mTimeLeftViews = (LinearLayout) v.findViewById(R.id.timeleft_all);

        /*
        Finds the "last time gotten ticket view"
         */
        mHistoryView = (TextView) v.findViewById(R.id.ticket_last_gotten);

        /*
        Finds the button and binds a listener to it.
         */
        mTicketButton = (Button) v.findViewById(R.id.button_get_ticket);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCard.getCharge() > mCard.useCharge(10)){
                    mCurrentTicket = new Ticket();
                    hasTicket();
                }
            }
        };
        mTicketButton.setOnClickListener(listener);

        return v;
    }

    private void hasTicket() {
        this.updateLastTicket();
        this.updateCharge();
        mTicketButton.setVisibility(View.INVISIBLE);

        final long time = (mCurrentTicket.mValidTo - System.currentTimeMillis());

        if (time < 0){
            hasNotTicket();
            return;
        }

        /*
        This will update the minute text once every second.
         */
        CountDownTimer timer = new CountDownTimer(time,500) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftText.setText(millisToMinutes(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                hasNotTicket();
            }
        };
        timer.start();

        /*
        Animate the progress bar smoothly.
         */
        ObjectAnimator anim = ObjectAnimator.ofInt(mProgressbar, "progress",  0);
        anim.setInterpolator(new LinearInterpolator());
        anim.setDuration(time*2);
        anim.start();
        mTimeLeftViews.setVisibility(View.VISIBLE);
    }

    private String millisToMinutes(long millisUntilFinished) {
        String min;
        double time = millisUntilFinished;
        time /= 1000 * 60;
        min = Integer.toString((int) Math.ceil(time));
        return min;
    }

    private void hasNotTicket() {
        mTimeLeftText.setText(null);
        mTimeLeftViews.setVisibility(View.INVISIBLE);
        mTicketButton.setVisibility(View.VISIBLE);

        mProgressbar.setProgress(mProgressbar.getMax());
        mCurrentTicket = null;
    }

    private void updateCharge() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        mCurrencyView.setText(formatter.format(mCard.getCharge()));
    }

    private void updateLastTicket(){
        mHistoryView.setText(DateFormat.getInstance().format(System.currentTimeMillis()));
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
