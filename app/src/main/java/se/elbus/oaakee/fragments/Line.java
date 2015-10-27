package se.elbus.oaakee.fragments;

import java.util.ArrayList;

public class Line {

    private String mNumber;
    private String mName;
    private ArrayList<Stop> mStops;

    public Line(String number, String name) {
        mNumber = number;
        mName = name;
        mStops = new ArrayList<>();
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

    public ArrayList<Stop> getStops() {
        return mStops;
    }

    public void setStops(ArrayList<Stop> stops) {
        mStops = stops;
    }
}
