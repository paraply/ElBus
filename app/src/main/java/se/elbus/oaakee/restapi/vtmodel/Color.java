package se.elbus.oaakee.restapi.vtmodel;


import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;

/**
 * Created by paraply on 2015-10-04.
 */
public class Color implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Color> CREATOR = new Parcelable.Creator<Color>() {
        @Override
        public Color createFromParcel(Parcel in) {
            return new Color(in);
        }

        @Override
        public Color[] newArray(int size) {
            return new Color[size];
        }
    };
    @Attribute(name = "fgColor", required = false) // Foreground color
    public String fgColor;
    @Attribute(name = "bgColor", required = false) // Background color
    public String bgColor;
    @Attribute(name = "stroke", required = false) // Stroke true or false
    public String stroke;


    public Color() {
    }

    protected Color(Parcel in) {
        fgColor = in.readString();
        bgColor = in.readString();
        stroke = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fgColor);
        dest.writeString(bgColor);
        dest.writeString(stroke);
    }

}
