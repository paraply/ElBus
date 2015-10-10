package se.elbus.oaakee.REST_API.VT_Model;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;

/**
 * Created by paraply on 2015-10-04.
 */
public class StopLocation implements Parcelable {
    @Attribute(name="name")
    public String name;

    @Attribute (name="id")
    public String id;

    @Attribute (name="lat")
    public String lat;

    @Attribute (name="lon")
    public String lon;

    @Attribute (name="track", required = false)
    public String track;

    @Attribute (name="weight", required = false) // 0 to 32767
    public String weight;

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<StopLocation> CREATOR
            = new Parcelable.Creator<StopLocation>() {
        public StopLocation createFromParcel(Parcel in) {
            return new StopLocation(in);
        }

        public StopLocation[] newArray(int size) {
            return new StopLocation[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(lat);
        dest.writeString(lon);
        dest.writeString(track);
        dest.writeString(weight);
    }

    /** recreate object from parcel */
    private StopLocation(Parcel in) {
        name = in.readString();
        id = in.readString();
        lat = in.readString();
        lon = in.readString();
        track = in.readString();
        weight = in.readString();
    }


}