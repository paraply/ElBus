package se.elbus.oaakee.REST_API.VT_Model;

import org.simpleframework.xml.Attribute;

/**
 * Created by paraply on 2015-10-04.
 */
public class Note {
    @Attribute(name = "text") // Note text
    public String text;

    @Attribute(name = "routeIdxFrom")
    public String routeIdxFrom;

    @Attribute(name = "routeIdxTo")
    public String routeIdxTo;
}
