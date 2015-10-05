package se.elbus.oaakee;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Anton on 2015-09-30.
 * ToDo:
 *  Service to repeatedly check the next stop for the current bus.
 *  Check if stop matches the chosen destination.
 *  If so, send push notification.
 */

// To start, use AlarmService.setServiceAlarm(getActivity(), true);
// The activity using the service must have a method equivalent to the newIntent method in this class (for the callback from the notification)

public class AlarmService extends IntentService {

    private static final String TAG = "AlarmService";
    //Minimum interval from 5.1 is 60 seconds, this will be rounded up
    private static final int POLL_INTERVAL = 1000 * 5;

    private String busID = "Ericsson$Vin_Num_001";
    private String destination = "Lindholmen";

    ECity_Client client = new ECity_Client();

    //Equivalent method must be in activity using this service
    public static Intent newIntent(Context context) {
        return new Intent(context, AlarmService.class);
    }

    public AlarmService() {
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
        if(!isNetworkAvailableAndConnected()) {
            return;
        }

        Log.i(TAG, "AlarmService intent received");

        Calendar fiveSeconds = Calendar.getInstance();
        fiveSeconds.add(Calendar.SECOND, -5);

        client.get_bus_sensor(busID, fiveSeconds.getTime(), Calendar.getInstance().getTime(), "Ericsson$Next_Stop");

        //How do I get the callback?

        if(false) {
            SendNotification();
        }
    }

    private void SendNotification() {
        Resources resources = getResources();
        Intent i = TestActivity.newIntent(this);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

        //Strings should be in resources, not hardcoded
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker("Time to get off yo")
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle("Stop")
                .setContentText("Hammer time")
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, notification);
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }
}
