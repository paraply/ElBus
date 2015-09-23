package com.elbus.ake;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.widget.Toast;

/**
 * Created by TH on 2015-09-23.
 */
public class Control {

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
