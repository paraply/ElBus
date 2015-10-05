package se.elbus.oaakee.REST_API.VT_Model;


import org.simpleframework.xml.Attribute;

/**
 * Created by paraply on 2015-10-04.
 */
public class Color {
    @Attribute(name="fgColor", required = false) // Foreground color
    public String fgColor;
    @Attribute (name="bgColor", required = false) // Background color
    public String bgColor;
    @Attribute (name="stroke", required = false) // Stroke true or false
    public String stroke;

}
