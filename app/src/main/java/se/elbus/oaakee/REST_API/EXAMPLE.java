package se.elbus.oaakee.REST_API;

import android.util.Log;

import java.util.Calendar;
import java.util.List;

import se.elbus.oaakee.REST_API.EC_Model.Bus_info;
import se.elbus.oaakee.REST_API.VT_Model.Departure;
import se.elbus.oaakee.REST_API.VT_Model.DepartureBoard;
import se.elbus.oaakee.REST_API.VT_Model.JourneyDetail;
import se.elbus.oaakee.REST_API.VT_Model.LocationList;
import se.elbus.oaakee.REST_API.VT_Model.StopLocation;
import se.elbus.oaakee.REST_API.VT_Model.Stop;


/**
 * Created by paraply on 2015-10-05.
 */
public class EXAMPLE implements VT_Callback, EC_Callback {
    VT_Client vast;
    EC_Client ecity;

    public EXAMPLE(){
        vast = new VT_Client(this);
        vast.get_nearby_stops("57.703834&", "11.966404", "30", "1000");
        Calendar hundred_seconds_old = Calendar.getInstance();
        hundred_seconds_old.add(Calendar.SECOND, -100);
        ecity = new EC_Client(this);
        ecity.get_bus_sensor("Ericsson$Vin_Num_001", hundred_seconds_old.getTime(), Calendar.getInstance().getTime(), "Ericsson$GPS");
        ecity.get_bus_sensor("", hundred_seconds_old.getTime(), Calendar.getInstance().getTime(), "Ericsson$GPS");
        ecity.get_bus_resource("Ericsson$Vin_Num_001", hundred_seconds_old.getTime(), Calendar.getInstance().getTime(), "Ericsson$Latitude_Value");
        ecity.get_bus_resource("", hundred_seconds_old.getTime(), Calendar.getInstance().getTime(), "Ericsson$Latitude_Value");
    }



    @Override
    public void got_journey_details(JourneyDetail journeyDetail) {
        for (Stop s : journeyDetail.stop) {
            Log.i("### LINE STOPS @", s.name + " WHEN: " + s.arrTime);
        }
    }

    @Override
    public void got_nearby_stops(LocationList locationList) {
        for (StopLocation s : locationList.stoplocation) { // List all nearby stops
            Log.i("### NEAR STOP", s.name + " ID:" + s.id + " TRACK:" +  s.track );
        }
        StopLocation closest = locationList.stoplocation.get(0); // The closest stop is at the top of the list
        Log.i("### CLOSEST STOP", closest.name + " ID:" + closest.id + " TRACK:" +  closest.track );
        vast.get_departure_board(closest.id); // Get departures from this stop
    }

    @Override
    public void got_departure_board(DepartureBoard departureBoard) {

        for (Departure d : departureBoard.departure) { // List all the departures from this stop
            Log.i("### DEPARTURES: ", d.name  + " SHORT NAME: " + d.sname + " DIRECTION: " + d.direction);
            if (d.sname.equals("11") && d.direction.equals("Bergsjön")){ // If spårvagn 11 mot Bergsjön
                vast.get_journey_details(d.journeyDetailRef); // Get journey details from this journey
                return;
            }
        }
    }



    @Override
    public void got_sensor_data(List<Bus_info> bus_info) {
        for (Bus_info b : bus_info) {
            Log.i("### SENSOR RESULT", "BUS ID:" + b.gatewayId + " RESOURCE:" + b.resourceSpec + " VALUE:" + b.value + " TIME:" + b.timestamp);
        }
    }

    @Override
    public void got_sensor_data_from_all_buses(List<Bus_info> bus_info) {
        for (Bus_info b : bus_info) {
            Log.i("### SENSOR RESULT ALL", "BUS ID:" + b.gatewayId + " RESOURCE:" + b.resourceSpec + " VALUE:" + b.value + " TIME:" + b.timestamp);
        }
    }

    @Override
    public void got_reource_data(List<Bus_info> bus_info) {
        for (Bus_info b : bus_info) {
            Log.i("### RSRC RESULT", "BUS ID:" + b.gatewayId + " RESOURCE:" + b.resourceSpec + " VALUE:" + b.value + " TIME:" + b.timestamp);
        }
    }

    @Override
    public void got_reource_data_from_all_buses(List<Bus_info> bus_info) {
        for (Bus_info b : bus_info) {
            Log.i("### RSRC RESULT ALL", "BUS ID:" + b.gatewayId + " RESOURCE:" + b.resourceSpec + " VALUE:" + b.value + " TIME:" + b.timestamp);
        }
    }

    @Override
    public void got_error(String during_method, String error_msg) {
        Log.i("### ERR", "during: " + during_method + "-" + error_msg);
    }
}
