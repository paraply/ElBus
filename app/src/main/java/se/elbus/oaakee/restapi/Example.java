package se.elbus.oaakee.restapi;

import android.util.Log;

import se.elbus.oaakee.restapi.ecmodel.busInfo;
import se.elbus.oaakee.restapi.vtmodel.Departure;
import se.elbus.oaakee.restapi.vtmodel.DepartureBoard;
import se.elbus.oaakee.restapi.vtmodel.JourneyDetail;
import se.elbus.oaakee.restapi.vtmodel.LocationList;
import se.elbus.oaakee.restapi.vtmodel.Stop;
import se.elbus.oaakee.restapi.vtmodel.StopLocation;

import java.util.Calendar;
import java.util.List;


public class Example implements VTCallback, ECCallback {
    VTClient vast;
    ECClient ecity;

    public Example() {
        vast = new VTClient(this);
        vast.getNearbyStops("57.703834&", "11.966404", "30", "1000");
        Calendar hundred_seconds_old = Calendar.getInstance();
        hundred_seconds_old.add(Calendar.SECOND, -100);
        ecity = new ECClient(this);
        ecity.getBusSensor("Ericsson$Vin_Num_001", hundred_seconds_old.getTime(), Calendar.getInstance().getTime(), "Ericsson$GPS");
        ecity.getBusSensor("", hundred_seconds_old.getTime(), Calendar.getInstance().getTime(), "Ericsson$GPS");
        ecity.getBusResource("Ericsson$Vin_Num_001", hundred_seconds_old.getTime(), Calendar.getInstance().getTime(), "Ericsson$Latitude_Value");
        ecity.getBusResource("", hundred_seconds_old.getTime(), Calendar.getInstance().getTime(), "Ericsson$Latitude_Value");
    }


    @Override
    public void handleJourneyDetails(JourneyDetail journeyDetail) {
        for (Stop s : journeyDetail.stop) {
            Log.i("### LINE STOPS @", s.name + " WHEN: " + s.arrTime);
        }
    }

    @Override
    public void handleNearbyStops(LocationList locationList) {
        for (StopLocation s : locationList.stoplocation) { // List all nearby stops
            Log.i("### NEAR STOP", s.name + " ID:" + s.id + " TRACK:" + s.track);
        }
        StopLocation closest = locationList.stoplocation.get(0); // The closest stop is at the top of the list
        Log.i("### CLOSEST STOP", closest.name + " ID:" + closest.id + " TRACK:" + closest.track);
        vast.getDepartureBoard(closest.id); // Get departures from this stop
    }

    @Override
    public void handleDepartureBoard(DepartureBoard departureBoard) {

        for (Departure d : departureBoard.departure) { // List all the departures from this stop
            Log.i("### DEPARTURES: ", d.name + " SHORT NAME: " + d.sname + " DIRECTION: " + d.direction);
            if (d.sname.equals("11") && d.direction.equals("Bergsjön")) { // If spårvagn 11 mot Bergsjön

                vast.getJourneyDetails(d.journeyDetailRef); // Get journey details from this journey
                return;
            }
        }
    }


    @Override
    public void handleSensorData(List<busInfo> busInfo) {
        for (busInfo b : busInfo) {
            Log.i("### SENSOR RESULT", "BUS ID:" + b.gatewayId + " RESOURCE:" + b.resourceSpec + " VALUE:" + b.value + " TIME:" + b.timestamp);
        }
    }

    @Override
    public void handleSensorDataFromAllBuses(List<busInfo> busInfo) {
        for (busInfo b : busInfo) {
            Log.i("### SENSOR RESULT ALL", "BUS ID:" + b.gatewayId + " RESOURCE:" + b.resourceSpec + " VALUE:" + b.value + " TIME:" + b.timestamp);
        }
    }

    @Override
    public void handleResourceData(List<busInfo> busInfo) {
        for (busInfo b : busInfo) {
            Log.i("### RSRC RESULT", "BUS ID:" + b.gatewayId + " RESOURCE:" + b.resourceSpec + " VALUE:" + b.value + " TIME:" + b.timestamp);
        }
    }

    @Override
    public void handleResourceDataFromAllBuses(List<busInfo> busInfo) {
        for (busInfo b : busInfo) {
            Log.i("### RSRC RESULT ALL", "BUS ID:" + b.gatewayId + " RESOURCE:" + b.resourceSpec + " VALUE:" + b.value + " TIME:" + b.timestamp);
        }
    }

    @Override
    public void handleError(String during_method, String error_msg) {
        Log.i("### ERR", "during: " + during_method + "-" + error_msg);
    }
}
