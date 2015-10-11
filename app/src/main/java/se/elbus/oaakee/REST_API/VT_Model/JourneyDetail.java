package se.elbus.oaakee.REST_API.VT_Model;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paraply on 2015-10-04.
 */
public class JourneyDetail implements Parcelable {

    public JourneyDetail(){}

    @Attribute(name="noNamespaceSchemaLocation")
    public String noNamespaceSchemaLocation;

    @Attribute (name="servertime")
    public String servertime;

    @Attribute (name="serverdate")
    public String serverdate;

    @ElementList(entry = "Stop", inline = true)
    public List<Stop> stop;

    @Element (name = "GeometryRef")
    public GeometryRef geometryRef;

    @ElementList (entry = "JourneyName", inline = true)
    public List<JourneyName> JourneyName;

    @ElementList (entry = "JourneyType", inline = true)
    public List<JourneyType> journeyType;

    @ElementList (entry = "JourneyId", inline = true)
    public List<JourneyId> journeyId;

    @Element (name = "Color")
    public Color color;

    @ElementList (entry = "Note", inline =  true, required = false)
    public Note note;

    @ElementList (entry = "Direction",  inline = true, required = false)
    public List<Direction> direction;

    protected JourneyDetail(Parcel in) {
        noNamespaceSchemaLocation = in.readString();
        servertime = in.readString();
        serverdate = in.readString();
        if (in.readByte() == 0x01) {
            stop = new ArrayList<Stop>();
            in.readList(stop, Stop.class.getClassLoader());
        } else {
            stop = null;
        }
        geometryRef = (GeometryRef) in.readValue(GeometryRef.class.getClassLoader());
        if (in.readByte() == 0x01) {
            JourneyName = new ArrayList<JourneyName>();
            in.readList(JourneyName, JourneyName.class.getClassLoader());
        } else {
            JourneyName = null;
        }
        if (in.readByte() == 0x01) {
            journeyType = new ArrayList<JourneyType>();
            in.readList(journeyType, JourneyType.class.getClassLoader());
        } else {
            journeyType = null;
        }
        if (in.readByte() == 0x01) {
            journeyId = new ArrayList<JourneyId>();
            in.readList(journeyId, JourneyId.class.getClassLoader());
        } else {
            journeyId = null;
        }
        color = (Color) in.readValue(Color.class.getClassLoader());
        note = (Note) in.readValue(Note.class.getClassLoader());
        if (in.readByte() == 0x01) {
            direction = new ArrayList<Direction>();
            in.readList(direction, Direction.class.getClassLoader());
        } else {
            direction = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(noNamespaceSchemaLocation);
        dest.writeString(servertime);
        dest.writeString(serverdate);
        if (stop == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(stop);
        }
        dest.writeValue(geometryRef);
        if (JourneyName == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(JourneyName);
        }
        if (journeyType == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(journeyType);
        }
        if (journeyId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(journeyId);
        }
        dest.writeValue(color);
        dest.writeValue(note);
        if (direction == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(direction);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<JourneyDetail> CREATOR = new Parcelable.Creator<JourneyDetail>() {
        @Override
        public JourneyDetail createFromParcel(Parcel in) {
            return new JourneyDetail(in);
        }

        @Override
        public JourneyDetail[] newArray(int size) {
            return new JourneyDetail[size];
        }
    };
}
