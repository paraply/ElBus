package se.elbus.oaakee.restapi.vtmodel;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by paraply on 2015-10-04.
 */
@Root(name = "DepartureBoard")
public class DepartureBoard {

    @Attribute(name = "noNamespaceSchemaLocation")
    public String noNamespaceSchemaLocation;

    @Attribute(name = "servertime")
    public String servertime;

    @Attribute(name = "serverdate")
    public String serverdate;

    @Attribute(name = "error", required = false) // Error code if error occurred
    public String error;

    @ElementList(entry = "Departure", inline = true) // List of departures
    public List<Departure> departure; // = new ArrayList<StopLocation>()
}
