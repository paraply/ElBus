package se.elbus.oaakee.restapi.vtmodel;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;

/**
 * Created by paraply on 2015-10-04.
 */
public class Note implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
    @Attribute(name = "text") // Note text
    public String text;

    @Attribute(name = "routeIdxFrom")
    public String routeIdxFrom;

    @Attribute(name = "routeIdxTo")
    public String routeIdxTo;

    public Note() {
    }

    protected Note(Parcel in) {
        text = in.readString();
        routeIdxFrom = in.readString();
        routeIdxTo = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(routeIdxFrom);
        dest.writeString(routeIdxTo);
    }

}
