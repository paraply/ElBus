package se.elbus.oaakee.REST_API.VT_Model;

import org.simpleframework.xml.Attribute;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by paraply on 2015-10-04.
 */
public class JourneyDetailRef {
    @Attribute()
    public String ref;


    public String getRef(){ //Use this to get real url without bad %25 and other stuff
        try {
            return URLDecoder.decode(ref, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

}
