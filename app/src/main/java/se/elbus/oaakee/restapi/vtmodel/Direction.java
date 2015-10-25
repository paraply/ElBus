package se.elbus.oaakee.restapi.vtmodel;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;

/**
 * Created by paraply on 2015-10-04.
 */
public class Direction implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Direction> CREATOR = new Parcelable.Creator<Direction>() {
        @Override
        public Direction createFromParcel(Parcel in) {
            return new Direction(in);
        }

        @Override
        public Direction[] newArray(int size) {
            return new Direction[size];
        }
    };
    @Attribute(name = "routeIdxFrom") //Start of validity on total route
    public String routeIdxFrom;

    @Attribute(name = "routeIdxTo") // End of validity on total route
    public String routeIdxTo;

    public Direction() {
    }

    protected Direction(Parcel in) {
        routeIdxFrom = in.readString();
        routeIdxTo = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(routeIdxFrom);
        dest.writeString(routeIdxTo);
    }
}
