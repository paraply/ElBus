package se.elbus.oaakee.REST_API.VT_Model;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by paraply on 2015-10-04.
 */
public class JourneyDetailRef implements Parcelable{

    public JourneyDetailRef(){}

    @Attribute()
    public String ref;


    public JourneyDetailRef(String ref_url){
        ref = ref_url;
    }


    public String getRef(){ //Use this to get real url without bad %25 and other stuff
        try {
            return URLDecoder.decode(ref, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<JourneyDetailRef> CREATOR
            = new Parcelable.Creator<JourneyDetailRef>() {
        public JourneyDetailRef createFromParcel(Parcel in) {
            return new JourneyDetailRef(in);
        }

        public JourneyDetailRef[] newArray(int size) {
            return new JourneyDetailRef[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ref);
    }

    /** recreate object from parcel */
    private JourneyDetailRef(Parcel in) {
        ref = in.readString();
    }
}
