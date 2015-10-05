package se.elbus.oaakee.REST_API.VT_Model;

import org.simpleframework.xml.Attribute;

/**
 * Created by paraply on 2015-10-04.
 */
public class StopLocation {
    @Attribute(name="name")
    public String name;

    @Attribute (name="id")
    public String id;

    @Attribute (name="lat")
    public String lat;

    @Attribute (name="lon")
    public String lon;

    @Attribute (name="track", required = false)
    public String track;

    @Attribute (name="weight", required = false) // 0 to 32767
    public String weight;
}