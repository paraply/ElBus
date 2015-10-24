package se.elbus.oaakee.REST_API.VT_Model;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;

/**
 * Created by paraply on 2015-10-04.
 */
public class JourneyType implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<JourneyType> CREATOR = new Parcelable.Creator<JourneyType>() {
        @Override
        public JourneyType createFromParcel(Parcel in) {
            return new JourneyType(in);
        }

        @Override
        public JourneyType[] newArray(int size) {
            return new JourneyType[size];
        }
    };
    @Attribute(name = "type")
    public String type;

    @Attribute(name = "routeIdxFrom")
    public String routeIdxFrom;

    @Attribute(name = "routeIdxTo")
    public String routeIdxTo;

    public JourneyType() {
    }

    protected JourneyType(Parcel in) {
        type = in.readString();
        routeIdxFrom = in.readString();
        routeIdxTo = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(routeIdxFrom);
        dest.writeString(routeIdxTo);
    }
}
