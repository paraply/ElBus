package se.elbus.oaakee;

import java.util.ArrayList;

public class BusStop {

    private String mName;
    private ArrayList<Bus> mBuses;

    public BusStop(String name) {
        mName = name;
        mBuses = new ArrayList<>();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public ArrayList<Bus> getBuses() {
        return mBuses;
    }

    public void setBuses(ArrayList<Bus> buses) {
        mBuses = buses;
    }
}
