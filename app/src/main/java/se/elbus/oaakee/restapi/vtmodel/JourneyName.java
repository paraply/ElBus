package se.elbus.oaakee.restapi.vtmodel;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;

/**
 * Created by paraply on 2015-10-04.
 */
public class JourneyName implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<JourneyName> CREATOR = new Parcelable.Creator<JourneyName>() {
        @Override
        public JourneyName createFromParcel(Parcel in) {
            return new JourneyName(in);
        }

        @Override
        public JourneyName[] newArray(int size) {
            return new JourneyName[size];
        }
    };
    @Attribute(name = "name")
    public String name;

    @Attribute(name = "routeIdxFrom")
    public String routeIdxFrom;

    @Attribute(name = "routeIdxTo")
    public String routeIdxTo;

    public JourneyName() {
    }

    protected JourneyName(Parcel in) {
        name = in.readString();
        routeIdxFrom = in.readString();
        routeIdxTo = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(routeIdxFrom);
        dest.writeString(routeIdxTo);
    }
}
