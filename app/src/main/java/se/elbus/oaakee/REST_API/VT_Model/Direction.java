package se.elbus.oaakee.REST_API.VT_Model;

import org.simpleframework.xml.Attribute;

/**
 * Created by paraply on 2015-10-04.
 */
public class Direction {
    @Attribute(name="routeIdxFrom") //Start of validity on total route
    public String routeIdxFrom;

    @Attribute (name="routeIdxTo") // End of validity on total route
    public String routeIdxTo;
}
