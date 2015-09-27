package com.elbus.ake;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Ake extends Activity implements View.OnClickListener
{

    /**
     * Data Variables
     */
    ArrayList<String> busMacs;
    List<String> busIds;
    int size;
    WifiManager wifi;
    List<ScanResult> results;

    ArrayList<String[]> buses;

    /**
     * UI Variables
     */
    ListView lv;
    Button buttonScan;
    SimpleAdapter adapter;
    String itemKey = "key";
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<>(10);
    private Ake.WifiReceiver receiver;

    /**
     * This is run when the activity is created.
     * @param savedInstanceState is just the saved instance.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        /*
          TODO: Come up with a better way to import the buses into the application.
         */
        this.buses = new ArrayList<>(10);
        Resources resources = this.getResources();
        this.buses.add(resources.getStringArray(R.array.b1));
        this.buses.add(resources.getStringArray(R.array.b2));
        this.buses.add(resources.getStringArray(R.array.b3));
        this.buses.add(resources.getStringArray(R.array.b4));
        this.buses.add(resources.getStringArray(R.array.b5));
        this.buses.add(resources.getStringArray(R.array.b6));
        this.buses.add(resources.getStringArray(R.array.b7));
        this.buses.add(resources.getStringArray(R.array.b8));
        this.buses.add(resources.getStringArray(R.array.b9));
        this.buses.add(resources.getStringArray(R.array.b10));
        this.busMacs = new ArrayList<>(1);


        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);

        this.buttonScan = (Button) this.findViewById(R.id.buttonScan);
        this.buttonScan.setOnClickListener(this);
        this.lv = (ListView) this.findViewById(R.id.list);

        this.adapter = new SimpleAdapter(this, this.arraylist, R.layout.row, new String[] {this.itemKey}, new int[] { R.id.list_value });
        this.lv.setAdapter(this.adapter); // Connects the adapter between the data and the GUI

        if(this.receiver == null) {
            this.receiver = new WifiReceiver();
        }
        this.startScan(this, this.receiver);

    }

    /**
     * This is wun when the activity is stopped.
     */
    @Override
    public void onStop() {
        super.onStop();
        if(this.receiver != null)
            this.unregisterReceiver(this.receiver);
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    /**
     * This is run when a bound view is clicked.
     * @param view is the view clicked.
     */
    @Override
    public void onClick(View view)
    {
        this.wifi.startScan();
        Toast.makeText(this, "Scanning..." + this.size, Toast.LENGTH_SHORT).show();
    }

    /**
     * This is to initiate a scan, given a target reciever.
     * @param context is the current context to use the wifi from.
     * @param target is what will be bound to receive the list of networks.
     */
    public void startScan(Activity context, BroadcastReceiver target){
        if(this.wifi == null)
            this.wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        /*
        TODO: Revise whether it's ok to always start wifi here.
         */
        this.wifi.setWifiEnabled(true);
        context.registerReceiver(target, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        this.wifi.startScan();
    }

    /**
     * This will return the id numbers of the buses (if duplicates).
     * @param macs are the mac-adresses of the buses.
     * @return an arraylist of id numbers.
     */
    public ArrayList<String> getBuses (ArrayList<String> macs){
        ArrayList<String> result = new ArrayList<>(1);
        for (String s : macs){
            for (String[] b : this.buses){
                if (b[3].matches(s)) // TODO: Not hardcode the number 3 into this but rather another dynamic way of reading the data from the xml doc.
                {
                    result.add(b[0]); // TODO: Not hardcode the number 0 into this but rather another dynamic way of reading the data from the xml doc.
                }
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
            Ake.this.arraylist.clear();
            Ake.this.results = Ake.this.wifi.getScanResults();
            Ake.this.size = Ake.this.results.size();

            Ake.this.size -= 1;
            while (Ake.this.size >= 0) {
                ScanResult result = Ake.this.results.get(Ake.this.size);

                HashMap<String, String> item = new HashMap<>();
                if (result.SSID.contains(Ake.this.getResources().getString(R.string.buswifiname))) {
                    Ake.this.busMacs.add(result.BSSID);
                    item.put(Ake.this.itemKey, result.SSID + " MAC: " + result.BSSID);
                    Ake.this.arraylist.add(item);
                }
                Ake.this.size--;
            }
            Ake.this.busIds = Ake.this.getBuses(Ake.this.busMacs);
            Ake.this.adapter.notifyDataSetChanged();

        }
    }
}