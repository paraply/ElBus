package se.elbus.oaakee.buses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * This class is used to scan wifi networks to find the closest bus.
 */
public abstract class WifiFinder extends BroadcastReceiver {
    private final String mWifiName;

    /**
     * Constructor for the class.
     *
     * @param context   is the context to first scan wifi from.
     * @param mWifiName is the exact name of the network to find.
     */
    public WifiFinder(Context context, String mWifiName) {
        this.mWifiName = mWifiName;
        this.scan(context);
    }

    /**
     * Will compare the signals of two ScanResults.
     *
     * @return false is s2 is closer, otherwise true.
     */
    public static boolean isCloser(ScanResult s1, ScanResult s2) {
        return s1.level - s2.level >= 0;
    }

    /**
     * This is used if one wants to do a scan manually.
     *
     * @param context is the context to scan from.
     */
    public void scan(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        Log.i("WifiFinder", "Looking for wifi");
        /*
        TODO: Save wifi state to change it back later.
         */
        if (wifi.setWifiEnabled(true)) {
            context.registerReceiver(this, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifi.startScan();
        }
    }

    /**
     * This method will take the received data and find the closest one that matches the defined
     * search expression. If it can't find a result, it's going to return NULL TODO: Make this class
     * NOT send NULL to handleData().
     *
     * @param context is the context to take the wifi from.
     * @param intent  is the type of call received. (Example: SCAN_RESULTS_AVAILABLE_ACTION)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        context.unregisterReceiver(this);
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        ScanResult result = null;
        for (ScanResult sr : wifi.getScanResults()) {
            /*
             * It it matches and we don't have anything, we save it.
             * If we find another one with a better signal, that one will replace the previous one.
             */
            if (sr.SSID.contentEquals(mWifiName)) {
                if (result == null) {
                    result = sr;
                } else {
                    if (isCloser(sr, result)) {
                        result = sr;
                    }
                }
            }
        }

        /*
         TODO: Change wifi state depending on what it was before!
         */
        this.handleData(result);
    }

    /**
     * Default implementation will try to find the dgw of the bus associated with the mac-address.
     * If it can't find the bus, it will return NULL. TODO: Make it NOT return NULL!
     *
     * @param wifi is the closest wifi matching the search results.
     */
    public void handleData(ScanResult wifi) {
        if (wifi == null) {
            return;
        }
        Log.i("WifiFinder", "Found " + wifi.BSSID);
        Bus bus = Buses.findBusByMac(wifi.BSSID);
        receiveDgw(bus.dgw);
    }

    /**
     * Since we don't know when the scan is finished, this will need to be implemented whenever an
     * instance of this class is created.
     *
     * @param dgw is the dgw of the wifi found.
     */
    public abstract void receiveDgw(String dgw);

}
