package se.elbus.oaakee.restapi.vtmodel;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;

/**
 * Created by paraply on 2015-10-04.
 */
public class JourneyId implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<JourneyId> CREATOR = new Parcelable.Creator<JourneyId>() {
        @Override
        public JourneyId createFromParcel(Parcel in) {
            return new JourneyId(in);
        }

        @Override
        public JourneyId[] newArray(int size) {
            return new JourneyId[size];
        }
    };
    @Attribute(name = "id")
    public String id;

    @Attribute(name = "routeIdxFrom")
    public String routeIdxFrom;

    @Attribute(name = "routeIdxTo")
    public String routeIdxTo;

    public JourneyId() {
    }

    protected JourneyId(Parcel in) {
        id = in.readString();
        routeIdxFrom = in.readString();
        routeIdxTo = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(routeIdxFrom);
        dest.writeString(routeIdxTo);
    }
}
