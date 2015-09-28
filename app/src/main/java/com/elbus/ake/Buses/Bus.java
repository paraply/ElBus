package com.elbus.ake.Buses;

/**
 * This class us just used to hold the data of the individual buses.
 *
 * Created by TH on 2015-09-27.
 */
public class Bus {
    public final String dgw;
    public final String mac;
    public final String vin;
    public final String reg;
    public final String type;

    protected Bus(String dgw, String vin, String reg, String mac, String type) {
        this.dgw = dgw.toLowerCase();
        this.mac = mac.toLowerCase();
        this.vin = vin.toLowerCase();
        this.reg = reg.toLowerCase();
        this.type = type.toLowerCase();
    }
}
