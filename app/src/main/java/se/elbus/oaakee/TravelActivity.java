package se.elbus.oaakee;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TravelActivity extends AppCompatActivity {

    private static Spinner mBusStopSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);

        createBusStopList();
        createDeparturesList();
    }

    /**
     * Populates the bus stop spinner
     */
    private void createBusStopList() {
        String[] busStops = {"Chalmers", "Kapellplatsen", "Vasaplatsen", "Valand",
                "Kungsportsplatsen", "Brunnsparken", "Centralstationen", "Långanamnhållplatsen abcdefgh"};

        mBusStopSpinner = (Spinner) findViewById(R.id.busStopSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, busStops);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mBusStopSpinner.setAdapter(adapter);
    }

    /**
     * Creates a list of departures from an array of strings
     */
    private void createDeparturesList() {
        String[] departures = {"Lindholmen", "Tynnered", "Bergsjön", "Majorna"};

        ArrayAdapter<String> adapter = new DeparturesAdapter(this, departures);
        ListView departuresListView = (ListView) findViewById(R.id.departuresListView);
        departuresListView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
