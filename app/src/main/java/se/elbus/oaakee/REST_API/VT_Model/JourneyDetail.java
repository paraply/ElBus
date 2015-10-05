package se.elbus.oaakee.REST_API.VT_Model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.List;

/**
 * Created by paraply on 2015-10-04.
 */
public class JourneyDetail {

    @Attribute(name="noNamespaceSchemaLocation")
    public String noNamespaceSchemaLocation;

    @Attribute (name="servertime")
    public String servertime;

    @Attribute (name="serverdate")
    public String serverdate;

    @ElementList(entry = "Stop", inline = true)
    public List<Stop> stop;

    @Element (name = "GeometryRef")
    public GeometryRef geometryRef;

    @ElementList (entry = "JourneyName", inline = true)
    public List<JourneyName> JourneyName;

    @ElementList (entry = "JourneyType", inline = true)
    public List<JourneyType> journeyType;

    @ElementList (entry = "JourneyId", inline = true)
    public List<JourneyId> journeyId;

    @Element (name = "Color")
    public Color color;

    @ElementList (entry = "Note", inline =  true, required = false)
    public Note note;

    @ElementList (entry = "Direction",  inline = true, required = false)
    public List<Direction> direction;
}
