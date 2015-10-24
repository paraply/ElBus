package se.elbus.oaakee.REST_API.VT_Model;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;

/**
 * Created by paraply on 2015-10-04.
 */
public class Stop implements Parcelable {

    public static final Parcelable.Creator<Stop> CREATOR
            = new Parcelable.Creator<Stop>() {
        public Stop createFromParcel(Parcel in) {
            return new Stop(in);
        }

        public Stop[] newArray(int size) {
            return new Stop[size];
        }
    };
    @Attribute(name = "name")
    public String name;
    @Attribute(name = "id")
    public String id;
    @Attribute(name = "lon")
    public String lon;
    @Attribute(name = "lat")
    public String lat;
    @Attribute(name = "routeIdx", required = false)
    public String routeIdx;
    @Attribute(name = "arrTime", required = false)
    public String arrTime;
    @Attribute(name = "rtArrTime", required = false)
    public String rtArrTime;
    @Attribute(name = "arrDate", required = false)
    public String arrDate;
    @Attribute(name = "rtArrDate", required = false)
    public String rtArrDate;
    @Attribute(name = "depTime", required = false)
    public String depTime;
    @Attribute(name = "rtDepTime", required = false)
    public String rtDepTime;
    @Attribute(name = "depDate", required = false)
    public String depDate;
    @Attribute(name = "rtDepDate", required = false)
    public String rtDepDate;
    @Attribute(name = "track", required = false)
    public String track;
    @Attribute(name = "rtTrack", required = false)
    public String rtTrack;

    public Stop() {
    }


    /**
     * recreate object from parcel
     */
    private Stop(Parcel in) {
        name = in.readString();
        id = in.readString();
        lon = in.readString();
        lat = in.readString();
        routeIdx = in.readString();

        arrTime = in.readString();
        rtArrTime = in.readString();
        arrDate = in.readString();
        depTime = in.readString();
        rtDepTime = in.readString();

        depDate = in.readString();
        rtDepDate = in.readString();
        track = in.readString();
        rtTrack = in.readString();
    }

    public String getNameWithoutCity() {
        return name.substring(0, name.lastIndexOf(","));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(lon);
        dest.writeString(lat);
        dest.writeString(routeIdx);

        dest.writeString(arrTime);
        dest.writeString(rtArrTime);
        dest.writeString(arrDate);
        dest.writeString(depTime);
        dest.writeString(rtDepTime);

        dest.writeString(depDate);
        dest.writeString(rtDepDate);
        dest.writeString(track);
        dest.writeString(rtTrack);
    }
}
