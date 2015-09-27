package com.elbus.ake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.elbus.ake.Buses.Bus;
import com.elbus.ake.Buses.Buses;

/**
 * Created by TH on 2015-09-27.
 */
public abstract class WifiFinder extends BroadcastReceiver {

    private final String wifiName;
    private boolean wifiOff; /* Assumed to be false at first. */

    public WifiFinder(Context context, String wifiName) {
        this.wifiName = wifiName;

        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if(wifi.setWifiEnabled(true)){
            this.wifiOff = true;
            context.registerReceiver(this,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifi.startScan();
        }

    }

    /**
     * This method will take the received data and find the closest one that matches the defined search expression.
     * If it can't find a result, it's going to retun NULL TODO: Make this class NOT send NULL to handleData().
     * @param context is the context to take the wifi from.
     * @param intent TODO: Add description.
     */
    @Override
    public void onReceive(Context context, Intent intent){
        context.unregisterReceiver(this);
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        ScanResult result = null;
        for (ScanResult sr : wifi.getScanResults()){
            /*
             * If we don't have a result yet it will add it if matching the string,
             * otherwise it's going to replace the result if we get a better signal.
             * TODO: Add a better way of finding closest wifi.
             */
            if((sr.SSID.matches(this.wifiName) && (result == null)) ||
                    ((result != null) && isCloser(sr, result))) {
                result = sr;
            }
        }

        if(this.wifiOff){
            wifi.setWifiEnabled(false);
        }
        this.handleData(result);
    }

    /**
     * If s1 had a higher or the same level, this will return true.
     */
    public static boolean isCloser(ScanResult s1, ScanResult s2){
        return s1.level - s2.level >= 0;
    }

    /**
     * Default implementation will try to find the dgw of the bus associated with the mac-adress.
     * If it can't find the bus it will return NULL. TODO: Make it NOT return NULL!
     * @param wifi is the closest wifi matching the search results.
     */
    public void handleData(ScanResult wifi){
        if (wifi == null){
            return;
        }
        Bus bus = Buses.findByMac(wifi.BSSID);
        receiveDgw(bus.getDgw());
    }

    /**
     * This is needed to receive the dgw somewhere.
     * @param dgw is the dgw of the wifi found.
     */
    public abstract void receiveDgw(String dgw);

}
