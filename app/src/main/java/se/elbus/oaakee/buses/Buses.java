package se.elbus.oaakee.buses;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

import se.elbus.oaakee.R;

/**
 * This class is used to store all the buses in a practical way. TODO: Find a replacement.
 */
public class Buses {
    private static List<Bus> sBuses;

    /**
     * This will read in all the buses into memory. It will only read in the sBuses the first time
     * called.
     */
    public static void initBuses(Context context) {
        if (sBuses != null) {
            return;
        }
        ArrayList<String[]> temp = new ArrayList<>();
        Resources resources = context.getResources();
        temp.add(resources.getStringArray(R.array.b1));
        temp.add(resources.getStringArray(R.array.b2));
        temp.add(resources.getStringArray(R.array.b3));
        temp.add(resources.getStringArray(R.array.b4));
        temp.add(resources.getStringArray(R.array.b5));
        temp.add(resources.getStringArray(R.array.b6));
        temp.add(resources.getStringArray(R.array.b7));
        temp.add(resources.getStringArray(R.array.b8));
        temp.add(resources.getStringArray(R.array.b9));
        temp.add(resources.getStringArray(R.array.b10));

        sBuses = new ArrayList<>();

        /*
         * This will follow the arbitrary template used in the .xml file where the bus-data is stored.
         */
        for (String[] sa : temp) {
            Bus b = new Bus(sa[0], sa[1], sa[2], sa[3], sa[4]);
            sBuses.add(b);
        }


    }

    /**
     * This is to warn if we've forgotten to initialize this class before trying to read from it.
     */
    private static void checkInit() throws BusesNotLoadedException {
        if (sBuses == null) {
            throw new BusesNotLoadedException("You need to initialize the buses!");
        }
    }

    /**
     * This will search through all the buses to find the one that matches the input mac.
     *
     * @param mac is the mac-address to look for.
     * @return the bus if it's found. If it's not found, it will return an empty bus.
     */
    public static Bus findBusByMac(String mac) {
        checkInit();
        for (Bus b : sBuses) {
            if (b.mac.contentEquals(mac.toLowerCase())) {
                return b;
            }
        }
        return new Bus("", "", "", "", "");
    }


    public Bus findBusByDgw(String dgw) {
        checkInit();
        for (Bus b : sBuses) {
            if (b.dgw.contentEquals(dgw.toLowerCase())) {
                return b;
            }
        }
        return new Bus("", "", "", "", "");
    }


    public Bus findBusByReg(String reg) {
        checkInit();
        for (Bus b : sBuses) {
            if (b.reg.contentEquals(reg.toLowerCase())) {
                return b;
            }
        }
        return new Bus("", "", "", "", "");
    }


    public Bus findBusByVin(String vin) {
        checkInit();
        for (Bus b : sBuses) {
            if (b.vin.contentEquals(vin.toLowerCase())) {
                return b;
            }
        }
        return new Bus("", "", "", "", "");
    }
}
