package se.elbus.oaakee.buses;

/**
 * This class us just used to hold the data of the individual buses.
 */
public class Bus {
    public final String dgw;
    public final String mac;
    public final String vin;
    public final String reg;
    public final String type;

    protected Bus(String dgw, String vin, String reg, String mac, String type) {
        this.dgw = dgw;
        this.mac = mac.toLowerCase();
        this.vin = vin.toLowerCase();
        this.reg = reg.toLowerCase();
        this.type = type.toLowerCase();
    }
}
