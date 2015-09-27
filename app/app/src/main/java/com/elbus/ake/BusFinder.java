package com.elbus.ake;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BusFinder extends Activity implements View.OnClickListener
{

    /**
     * Data Variables
     */
    ArrayList<String> busMacs;
    ArrayList<String> busIds;
    int size = 0;
    WifiManager wifi;
    List<ScanResult> results;

    ArrayList<String[]> buses;

    /**
     * UI Variables
     */
    ListView lv;
    Button buttonScan;
    SimpleAdapter adapter;
    String ITEM_KEY = "key";
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<>();
    private WifiReceiver receiver;

    /**
     * This is run when the activity is created.
     * @param savedInstanceState is just the saved instance.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        /**
         * TODO: Come up with a better way to import the buses into the application.
         */
        buses = new ArrayList<>(10);
        buses.add(getResources().getStringArray(R.array.b1));
        buses.add(getResources().getStringArray(R.array.b2));
        buses.add(getResources().getStringArray(R.array.b3));
        buses.add(getResources().getStringArray(R.array.b4));
        buses.add(getResources().getStringArray(R.array.b5));
        buses.add(getResources().getStringArray(R.array.b6));
        buses.add(getResources().getStringArray(R.array.b7));
        buses.add(getResources().getStringArray(R.array.b8));
        buses.add(getResources().getStringArray(R.array.b9));
        buses.add(getResources().getStringArray(R.array.b10));
        busMacs = new ArrayList<>(1);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        buttonScan = (Button) findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(this);
        lv = (ListView)findViewById(R.id.list);

        this.adapter = new SimpleAdapter(BusFinder.this, arraylist, R.layout.row, new String[] { ITEM_KEY }, new int[] { R.id.list_value });
        lv.setAdapter(this.adapter); // Connects the adapter between the data and the GUI

        if(receiver == null)
            receiver = new WifiReceiver();
        startScan(this, receiver);

    }

    /**
     * This is wun when the activity is stopped.
     */
    @Override
    public void onStop() {
        super.onStop();
        if(receiver != null)
            this.unregisterReceiver(receiver);
    }

    /**
     * This is run when a bound view is clicked.
     * @param view is the view clicked.
     */
    public void onClick(View view)
    {
        wifi.startScan();
        Toast.makeText(this, "Scanning..." + size, Toast.LENGTH_SHORT).show();
    }

    /**
     * This is to initiate a scan, given a target reciever.
     * @param context is the current context to use the wifi from.
     * @param target is what will be bound to receive the list of networks.
     */
    public void startScan(Activity context, BroadcastReceiver target){
        if(wifi == null)
            wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        /*
        TODO: Revise whether it's ok to always start wifi here.
         */
        wifi.setWifiEnabled(true);
        context.registerReceiver(target, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifi.startScan();
    }

    /**
     * This will return the id numbers of the buses (if duplicates).
     * @param macs are the mac-adresses of the buses.
     * @return an arraylist of id numbers.
     */
    public ArrayList<String> getBuses (ArrayList<String> macs){
        ArrayList<String> result = new ArrayList<>(1);
        for (String s : macs){
            for (String[] b : buses ){
                if (b[3].matches(s)) // TODO: Not hardcode the number 3 into this but rather another dynamic way of reading the data from the xml doc.
                    result.add(b[0]); // TODO: Not hardcode the number 0 into this but rather another dynamic way of reading the data from the xml doc.
            }
        }
        return result;
    }

    /**
     * This is the class handling the wifi scan results.
     */
    class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            arraylist.clear();
            results = wifi.getScanResults();
            size = results.size();

            size = size - 1;
            while (size >= 0) {
                ScanResult result = results.get(size);

                HashMap<String, String> item = new HashMap<>();
                if (result.SSID.contains(getResources().getString(R.string.buswifiname))) {
                    busMacs.add(result.BSSID);
                    item.put(ITEM_KEY, result.SSID + " MAC: " + result.BSSID);
                    arraylist.add(item);
                }
                size--;
            }
            busIds = getBuses(busMacs);
            adapter.notifyDataSetChanged();

        }
    }
}