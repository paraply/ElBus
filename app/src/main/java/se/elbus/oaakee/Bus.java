package se.elbus.oaakee;

import java.util.ArrayList;

public class Bus {

    private String mNumber;
    private String mName;
    private ArrayList<BusStop> mBusStops;

    public Bus(String number, String name) {
        mNumber = number;
        mName = name;
        mBusStops = new ArrayList<>();
    }
    
    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String number) {
        mNumber = number;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public ArrayList<BusStop> getBusStops() {
        return mBusStops;
    }

    public void setBusStops(ArrayList<BusStop> busStops) {
        mBusStops = busStops;
    }
}
