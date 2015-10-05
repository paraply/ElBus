package se.elbus.oaakee.REST_API.VT_Model;

import org.simpleframework.xml.Attribute;

/**
 * Created by paraply on 2015-10-04.
 */
public class JourneyId {
    @Attribute(name = "id")
    public String id;

    @Attribute(name = "routeIdxFrom")
    public String routeIdxFrom;

    @Attribute(name = "routeIdxTo")
    public String routeIdxTo;
}
