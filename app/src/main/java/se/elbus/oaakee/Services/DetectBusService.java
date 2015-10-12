package se.elbus.oaakee.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.util.Calendar;

import se.elbus.oaakee.Buses.WifiFinder;
import se.elbus.oaakee.R;

/**
 * Created by Anton on 2015-10-11.
 */
public class DetectBusService extends IntentService {

    private static final String TAG = "DetectBusService";

    //Minimum interval from 5.1 is 60 seconds, this will be rounded up
    private static final int POLL_INTERVAL = 1000 * 30;

    public static String dgwFound;
    public static boolean onBus;

    public static Intent newIntent(Context context) {
        return new Intent(context, AlarmService.class);
    }

    public DetectBusService() {
        super(TAG);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = AlarmService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        if(isOn) {
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
        WifiFinder wifiFinder = new WifiFinder(this, this.getString(R.string.buswifiname)) {
            @Override
            // Found an Dgw close to us. We assume we are on this bus
            public void receiveDgw(String dgw) {
                Log.i(TAG, "Found Wifi, dgw: " + dgw);
                dgwFound = dgw;
                onBus = true;
            }
        };
    }
}
