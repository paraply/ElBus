package se.elbus.oaakee.REST_API.VT_Model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by paraply on 2015-10-04.
 */
@Root(name = "LocationList")
public class LocationList {

    public LocationList(){}

    @Attribute(name="noNamespaceSchemaLocation") // Mostly unnecessary information
    public String noNamespaceSchemaLocation;

    @Attribute (name="servertime")              // The server time when request was performed
    public String servertime;

    @Attribute (name="serverdate")              // The server date when request was performed
    public String serverdate;

    @ElementList (entry = "StopLocation", inline = true)
    public List<StopLocation> stoplocation; // = new ArrayList<StopLocation>()

// Has coordlocation only if trip request. Maybe?
}