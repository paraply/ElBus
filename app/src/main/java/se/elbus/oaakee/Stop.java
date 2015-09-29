package se.elbus.oaakee;

import java.util.ArrayList;

public class Stop {

    private String mName;
    private ArrayList<Line> mBuses;

    public Stop(String name) {
        mName = name;
        mBuses = new ArrayList<>();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public ArrayList<Line> getBuses() {
        return mBuses;
    }

    public void setBuses(ArrayList<Line> buses) {
        mBuses = buses;
    }
}
