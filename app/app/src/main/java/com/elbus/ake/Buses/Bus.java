package com.elbus.ake.Buses;

/**
 * Created by TH on 2015-09-27.
 */
public class Bus {
    private final String dgw;
    private final String mac;
    private final String vin;
    private final String reg;
    private final String type;

    protected Bus(String dgw, String vin, String reg, String mac, String type) {
        this.dgw = dgw;
        this.mac = mac;
        this.vin = vin;
        this.reg = reg;
        this.type = type;
    }

    public String getDgw() {
        return this.dgw;
    }

    public String getMac() {
        return this.mac;
    }
    public String getVin() {return this.vin;  }
    public String getReg() {return this.reg;  }
    public String getType() {return this.type;}
}
