package com.elbus.ake.Buses;

import android.content.Context;
import android.content.res.Resources;

import com.elbus.ake.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TH on 2015-09-27.
 */
public class Buses{
    private static List<Bus> buses;

    /**
     * This will read in all the buses into memory.
     * It will only read in the buses the first time called.
     * @param context
     */
    public static void initBuses(Context context) {
        if (buses != null){
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
        for(String[] sa : temp){
            Bus b = new Bus(sa[0],sa[1],sa[2],sa[3],sa[4]);
            buses.add(b);
        }


    }

    private static void checkInit() throws BusNotLoadedException {
        if (buses == null){
            throw new BusNotLoadedException("You need to initialize the buses!");
        }
    }


    public static Bus findByMac(String mac) {
        checkInit();
        for (Bus b : buses){
            if(b.getMac().matches(mac)){
                return b;
            }
        }
        return new Bus("","","","","");
    }


    public Bus findByDgw(String dgw) {
        checkInit();
        for (Bus b : buses){
            if(b.getDgw().matches(dgw)){
                return b;
            }
        }
        return new Bus("","","","","");
    }


    public Bus findByReg(String reg) {
        checkInit();
        for (Bus b : buses){
            if(b.getReg().matches(reg)){
                return b;
            }
        }
        return new Bus("","","","","");
    }


    public Bus findByVin(String vin) {
        checkInit();
        for (Bus b : buses){
            if(b.getVin().matches(vin)){
                return b;
            }
        }
        return new Bus("","","","","");
    }
}
