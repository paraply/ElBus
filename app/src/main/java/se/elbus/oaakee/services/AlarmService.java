package se.elbus.oaakee.services;

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

import se.elbus.oaakee.MainActivity;
import se.elbus.oaakee.R;
import se.elbus.oaakee.restapi.ECCallback;
import se.elbus.oaakee.restapi.ECClient;
import se.elbus.oaakee.restapi.ecmodel.Bus_info;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Anton on 2015-09-30. Service to repeatedly check the next stop for the current bus.
 * Check if stop matches the chosen destination. If so, send push notification.
 */

// To start, use AlarmService.setServiceAlarm(getActivity(), true);
// The activity using the service must have a method equivalent to the newIntent method in this class (for the callback from the notification)

public class AlarmService extends IntentService implements ECCallback {

    private static final String TAG = "AlarmService";
    //Minimum interval from 5.1 is 60 seconds, this will be rounded up
    private static final int POLL_INTERVAL = 1000 * 30;
    private static String busID = "Ericsson$Vin_Num_001";
    private static String destination = "Lindholmen";
    ECClient client = new ECClient(this);

    public AlarmService() {
        super(TAG);
    }

    //Equivalent method must be in activity using this service
    public static Intent newIntent(Context context) {
        return new Intent(context, AlarmService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn, String bus, String dest) {
        Intent i = AlarmService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), POLL_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }

        busID = bus;
        destination = dest;
    }

    private void SendNotification() {
        Resources resources = getResources();
        Intent i = MainActivity.newIntent(this);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

        //Strings should be in resources, not hardcoded
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(getString(R.string.AlarmTicker))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(getString(R.string.AlarmTitle))
                .setContentText(getString(R.string.AlarmText))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
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

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetworkAvailableAndConnected()) {
            return;
        }

        Log.i(TAG, "AlarmService intent received for " + destination);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, -POLL_INTERVAL);

        client.get_bus_sensor(busID, calendar.getTime(), Calendar.getInstance().getTime(), "Ericsson$Next_Stop");
    }

    @Override
    public void got_sensor_data(List<Bus_info> bus_info) {
        if (bus_info == null) return;

        for (Bus_info b : bus_info) {
            Log.i("### SENSOR RESULT", "BUS ID:" + b.gatewayId + " RESOURCE:" + b.resourceSpec + " VALUE:" + b.value + " TIME:" + b.timestamp);
        }

        String lastResult = bus_info.get(bus_info.size() - 1).value;
        lastResult = lastResult.replace(' ', '_');
        lastResult = lastResult.replace('å', 'a');
        lastResult = lastResult.replace('Å', 'A');
        lastResult = lastResult.replace('ä', 'a');
        lastResult = lastResult.replace('Ä', 'A');
        lastResult = lastResult.replace('ö', 'o');
        lastResult = lastResult.replace('Ö', 'O');
        lastResult = lastResult.substring(0, lastResult.length()-1);
        Log.i(TAG, lastResult);
        int id = getResources().getIdentifier(lastResult, "string", getPackageName());
        Log.i(TAG, String.valueOf(id));
        String vtResult = "";
        if(id!=0) {
            vtResult = getString(id);
        }
        Log.i("Result as VT: ", vtResult);

        if (vtResult.equals(destination)) {
            SendNotification();
        }
    }

    @Override
    public void got_sensor_data_from_all_buses(List<Bus_info> bus_info) {
        if (bus_info == null) return;
        for (Bus_info b : bus_info) {
            Log.i("### SENSOR RESULT ALL", "BUS ID:" + b.gatewayId + " RESOURCE:" + b.resourceSpec + " VALUE:" + b.value + " TIME:" + b.timestamp);
        }
    }

    @Override
    public void got_reource_data(List<Bus_info> bus_info) {
        if (bus_info == null) return;
        for (Bus_info b : bus_info) {
            Log.i("### RSRC RESULT", "BUS ID:" + b.gatewayId + " RESOURCE:" + b.resourceSpec + " VALUE:" + b.value + " TIME:" + b.timestamp);
        }
    }

    @Override
    public void got_reource_data_from_all_buses(List<Bus_info> bus_info) {
        if (bus_info == null) return;
        for (Bus_info b : bus_info) {
            Log.i("### RSRC RESULT ALL", "BUS ID:" + b.gatewayId + " RESOURCE:" + b.resourceSpec + " VALUE:" + b.value + " TIME:" + b.timestamp);
        }
    }

    @Override
    public void got_error(String during_method, String error_msg) {
        Log.i("### ERR", "during: " + during_method + "-" + error_msg);
    }
}
