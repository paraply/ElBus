package se.elbus.oaakee.REST_API.VT_Model;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;

/**
 * Created by paraply on 2015-10-04.
 */
public class GeometryRef implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GeometryRef> CREATOR = new Parcelable.Creator<GeometryRef>() {
        @Override
        public GeometryRef createFromParcel(Parcel in) {
            return new GeometryRef(in);
        }

        @Override
        public GeometryRef[] newArray(int size) {
            return new GeometryRef[size];
        }
    };
    @Attribute()  // URL to geometry information
    public String ref;

    public GeometryRef() {
    }

    protected GeometryRef(Parcel in) {
        ref = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ref);
    }
}
