package se.elbus.oaakee.REST_API.VT_Model;

import org.simpleframework.xml.Attribute;

/**
 * Created by paraply on 2015-10-04.
 */
public class Stop {
    @Attribute(name="name")
    public String name;

    @Attribute(name="id")
    public String id;

    @Attribute(name="lon")
    public String lon;

    @Attribute(name="lat")
    public String lat;

    @Attribute(name="routeIdx", required = false)
    public String routeIdx;

    @Attribute(name="arrTime", required = false)
    public String arrTime;

    @Attribute(name="rtArrTime", required = false)
    public String rtArrTime;

    @Attribute(name="arrDate", required = false)
    public String arrDate;

    @Attribute(name="rtArrDate", required = false)
    public String rtArrDate;

    @Attribute(name="depTime", required = false)
    public String depTime;

    @Attribute(name="rtDepTime", required = false)
    public String rtDepTime;

    @Attribute(name="depDate", required = false)
    public String depDate;

    @Attribute(name="rtDepDate", required = false)
    public String rtDepDate;

    @Attribute(name="track", required = false)
    public String track;

    @Attribute(name="rtTrack", required = false)
    public String rtTrack;
}
