package se.elbus.oaakee.fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;

import se.elbus.oaakee.R;

public class PaymentFragment extends Fragment {

    private final long TICKET_TIME = 125000; // 2 minutes and 5 seconds.
    private final double TICKET_COST = 10;
    private ImageButton mTicketButton;
    private ProgressBar mProgressbar;
    private TextView mTimeLeftText;
    private LinearLayout mTimeLeftViews;
    private TextView mCurrencyView;
    private TextView mHistoryView;
    private CountDownTimer mTimer;
    private ObjectAnimator mAnim;
    private long mValidTo = 0;
    private double mCharge = 200;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_payment, container, false);

        mCurrencyView = (TextView) v.findViewById(R.id.chargeText);
        updateCharge();

        mProgressbar = (ProgressBar) v.findViewById(R.id.countdown_progressbar);

        mTimeLeftText = (TextView) v.findViewById(R.id.timeleft);
        mTimeLeftViews = (LinearLayout) v.findViewById(R.id.timeleft_all);

        mHistoryView = (TextView) v.findViewById(R.id.ticket_last_gotten);

        mTicketButton = (ImageButton) v.findViewById(R.id.button_get_ticket);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCharge > TICKET_COST) {
                    mCharge -= TICKET_COST;
                    mValidTo = System.currentTimeMillis() + TICKET_TIME;
                    hasTicket();
                }
            }
        };
        mTicketButton.setOnClickListener(listener);

        return v;
    }

    @Override
    public void onPause() {
        super.onDetach();
        if (mAnim != null)
            mAnim.cancel();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.onFinish();
        }
    }

    private void hasTicket() {
        this.updateLastTicket();
        this.updateCharge();
        mTicketButton.setVisibility(View.INVISIBLE);

        final long time = (mValidTo - System.currentTimeMillis());

        if (time < 0) {
            hasNotTicket();
            return;
        }

        /*
        This will update the minute text once every 5 seconds.
         */
        mTimer = new CountDownTimer(time, 5000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftText.setText(millisToMinutes(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                hasNotTicket();
            }
        };
        mTimer.start();

        /*
        Animate the progress bar smoothly.
         */
        mAnim = ObjectAnimator.ofInt(mProgressbar, "progress", mProgressbar.getMax(), 0);
        mAnim.setInterpolator(new LinearInterpolator());
        mAnim.setDuration(time * 2);
        mAnim.start();
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
        mValidTo = 0;
    }

    private void updateCharge() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        mCurrencyView.setText(formatter.format(mCharge));
    }

    private void updateLastTicket() {
        mHistoryView.setText(DateFormat.getInstance().format(System.currentTimeMillis()));
    }
}
