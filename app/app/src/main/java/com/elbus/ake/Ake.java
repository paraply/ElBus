package com.elbus.ake;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.elbus.ake.Buses.Buses;

import java.util.ArrayList;
import java.util.HashMap;

public class Ake extends Activity implements View.OnClickListener {
    /**
     * UI Variables
     */
    ListView lv;
    Button buttonScan;

    String itemKey = "key";
    SimpleAdapter adapter;
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<>(10);

    WifiFinder receiver;

    /**
     * This is run when the activity is created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);

        this.buttonScan = (Button) this.findViewById(R.id.buttonScan);
        this.buttonScan.setOnClickListener(this);
        this.lv = (ListView) this.findViewById(R.id.list);

        this.adapter = new SimpleAdapter(this, this.arraylist, R.layout.row, new String[]{this.itemKey}, new int[]{R.id.list_value});
        this.lv.setAdapter(this.adapter); // Connects the adapter between the data and the GUI

        Buses.initBuses(this);
        this.receiver =
                new WifiFinder(this, getResources().getString(R.string.buswifiname)) {
                    @Override
                    public void receiveDgw(String dgw) {
                        HashMap<String, String> item = new HashMap<>();
                        item.put(itemKey, dgw);
                        arraylist.add(item);

                        adapter.notifyDataSetChanged();
                    }
                };
    }

    /**
     * This is run when a bound view is clicked.
     */
    @Override
    public void onClick(View view) {
        this.receiver.scan(this);
        Toast.makeText(this, "Scanning...", Toast.LENGTH_SHORT).show();
    }
}