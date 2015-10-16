package se.elbus.oaakee.Services;

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
import java.util.List;

import se.elbus.oaakee.MainActivity;
import se.elbus.oaakee.R;
import se.elbus.oaakee.REST_API.EC_Callback;
import se.elbus.oaakee.REST_API.EC_Client;
import se.elbus.oaakee.REST_API.EC_Model.Bus_info;

/**
 * Created by Anton on 2015-09-30.
 * Service to repeatedly check the next stop for the current bus.
 * Check if stop matches the chosen destination.
 * If so, send push notification.
 */

// To start, use AlarmService.setServiceAlarm(getActivity(), true);
// The activity using the service must have a method equivalent to the newIntent method in this class (for the callback from the notification)

public class AlarmService extends IntentService implements EC_Callback {

    private static final String TAG = "AlarmService";
    //Minimum interval from 5.1 is 60 seconds, this will be rounded up
    private static final int POLL_INTERVAL = 1000 * 30;
    EC_Client client = new EC_Client(this);
    private static String busID = "Ericsson$Vin_Num_001";
    private static String destination = "Lindholmen";

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

        Log.i(TAG, "AlarmService intent received");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, POLL_INTERVAL);

        client.get_bus_sensor(busID, calendar.getTime(), Calendar.getInstance().getTime(), "Ericsson$Next_Stop");
    }

    @Override
    public void got_sensor_data(List<Bus_info> bus_info) {
        if (bus_info == null) return;

        if (bus_info.get(bus_info.size() - 1).value.equals(destination)) {
            SendNotification();
        }

        for (Bus_info b : bus_info) {
            Log.i("### SENSOR RESULT", "BUS ID:" + b.gatewayId + " RESOURCE:" + b.resourceSpec + " VALUE:" + b.value + " TIME:" + b.timestamp);
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
