package com.elbus.ake;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.widget.Toast;

/**
 * Created by TH on 2015-09-23.
 *
 * Mainly to control the hardware of the device.
 */
public class Control {
    /**
     * This will set wifi service depending on the parameter ON.
     *
     * @param ON is whether to start the wifi or not.
     * @param context is the current context the application is in.
     */
    public static void Wifi(boolean ON, Activity context){
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if(wifi.isWifiEnabled() != ON){

            wifi.setWifiEnabled(ON);
        }
        if(ON){
            Toast.makeText(context, "Enabling Wifi", Toast.LENGTH_LONG).show();

        }else{
            Toast.makeText(context, "Disabling Wifi", Toast.LENGTH_LONG).show();
        }

    }
}
