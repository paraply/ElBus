package se.elbus.oaakee.Buses;

import android.content.Context;
import android.content.res.Resources;

import com.elbus.ake.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to store all the buses in a practical way.
 * (Might use more memory than it has to) TODO: Find a replacement.
 * Created by TH on 2015-09-27.
 */
public class Buses {
    private static List<Bus> buses;

    /**
     * This will read in all the buses into memory.
     * It will only read in the buses the first time called.
     *
     * TODO: If more resources are added, we need to change this code to make it import it!!
     *
     * @param context
     */
    public static void initBuses(Context context) {
        if (buses != null) {
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

        buses = new ArrayList<>();

        /*
         * This will follow the arbitrary template used in the .xml file where the bus-data is stored.
         */
        for (String[] sa : temp) {
            Bus b = new Bus(sa[0], sa[1], sa[2], sa[3], sa[4]);
            buses.add(b);
        }


    }

    /**
     * This is to warn if we've forgotten to initialize this class before trying to read from it.
     * @throws BusesNotLoadedException
     */
    private static void checkInit() throws BusesNotLoadedException {
        if (buses == null) {
            throw new BusesNotLoadedException("You need to initialize the buses!");
        }
    }

    /**
     * This wil search through all the buses to find the one that matches the input mac.
     * @param mac is the mac-adress to look for.
     * @return the bus if it's found. If it's not found, it will return an empty bus.
     */
    public static Bus findByMac(String mac) {
        checkInit();
        for (Bus b : buses) {
            if (b.mac.contentEquals(mac.toLowerCase())) {
                return b;
            }
        }
        return new Bus("", "", "", "", "");
    }


    public Bus findByDgw(String dgw) {
        checkInit();
        for (Bus b : buses) {
            if (b.dgw.contentEquals(dgw.toLowerCase())) {
                return b;
            }
        }
        return new Bus("", "", "", "", "");
    }


    public Bus findByReg(String reg) {
        checkInit();
        for (Bus b : buses) {
            if (b.reg.contentEquals(reg.toLowerCase())) {
                return b;
            }
        }
        return new Bus("", "", "", "", "");
    }


    public Bus findByVin(String vin) {
        checkInit();
        for (Bus b : buses) {
            if (b.vin.contentEquals(vin.toLowerCase())) {
                return b;
            }
        }
        return new Bus("", "", "", "", "");
    }
}