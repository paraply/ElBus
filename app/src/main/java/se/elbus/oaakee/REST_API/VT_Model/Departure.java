package se.elbus.oaakee.REST_API.VT_Model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

/**
 * Created by paraply on 2015-10-04.
 */
public class Departure {

    @Attribute(name="name") // Name of the bus - "Bus 100"
    public String name;

    @Attribute(name="type") // Type of transport - "BUS", "TRAM", "BOAT"....
    public String type;

    @Attribute(name="sname") // Short name - "LERS" or "11"
    public String sname;

    @Attribute(name="stopid") // ID of the stop/station
    public String stopid;

    @Attribute(name="stop") // Contains the name of the stop/station
    public String stop;

    @Attribute(name="time") // Time format HH:MM
    public String time;

    @Attribute(name="date") // Date format YYYY-MM-DD
    public String date;

    @Attribute(name="journeyid") // ID of the journey
    public String journeyid;

    @Attribute(name="direction", required = false) // Direction information if available
    public String direction;

    @Attribute(name="booking", required = false) // Returns true if this line needs to be booked
    public String booking;

    @Attribute(name="night", required = false) // Returns true if is a night journey
    public String night;

    @Attribute(name="track", required = false) // Track A,B,C... if available
    public String track;

    @Attribute(name="rtTrack", required = false) // Real-Time track A,B,C... if available
    public String rtTrack;

    @Attribute(name="rtTime", required = false) // Real time information HH:MM if available
    public String rtTime;

    @Attribute(name="rtDate", required = false) // Real-time date YYYY-MM-DD if available
    public String rtDate;

    @Attribute(name="fgColor", required = false) // Foreground color of this line
    public String fgColor;

    @Attribute(name="bgColor", required = false) // Background color of this line
    public String bgColor;

    @Attribute(name="stroke", required = false) // Stroke style
    public String stroke;

    @Attribute(name="accessibility", required = false) // Wheelchair access + ramp or low floor. According to real time data
    public String accessibility;

    @Element (name = "JourneyDetailRef", required = false)
    public JourneyDetailRef journeyDetailRef;

}
