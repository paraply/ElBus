package se.elbus.oaakee.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import se.elbus.oaakee.R;
import se.elbus.oaakee.buses.WifiFinder;

import java.util.Calendar;

public class DetectBusService extends IntentService {

    private static final String TAG = "DetectBusService";

    //Minimum interval from 5.1 is 60 seconds, this will be rounded up
    private static final int POLL_INTERVAL = 1000 * 30;

    public static String dgwFound;
    public static boolean onBus = false;

    private static String mSource;
    private static String mDestination;
    private static String mTime;
    private static String mLine;

    public DetectBusService() {
        super(TAG);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, AlarmService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn, String s, String d, String t, String l) {
        //Check if nothing differs from last time setServiceAlarm was called
        if (s.equals(mSource) && d.equals(mDestination) && t.equals(mTime) && l.equals(mLine)) {
            return;
        }

        mSource = s;
        mDestination = d;
        mTime = t;
        mLine = l;
        Intent i = AlarmService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), POLL_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "DetectBusService intent received");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, POLL_INTERVAL);


        //Check wififinder for connection
        WifiFinder wifiFinder = new WifiFinder(this, this.getString(R.string.wifi_name)) {
            @Override
            // Found an Dgw close to us. We assume we are on this bus
            public void receiveDgw(String dgw) {
                Log.i(TAG, "Found Wifi, dgw: " + dgw);
                dgwFound = dgw;
                /**
                 *  This should probably be done somewhere else.
                 *  Should also check if bus we think we're on matches the choice made.
                 *  Does not matter for prototype since we only target line 55.
                 */
                if (!onBus) {
                    AlarmService.setServiceAlarm(DetectBusService.this, true, dgwFound, mDestination);
                    onBus = true;
                }
            }
        };
    }
}
