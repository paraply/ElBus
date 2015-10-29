package se.elbus.oaakee.restapi;


import se.elbus.oaakee.restapi.vtmodel.DepartureBoard;
import se.elbus.oaakee.restapi.vtmodel.JourneyDetail;
import se.elbus.oaakee.restapi.vtmodel.LocationList;

public interface VTCallback {
    void handleJourneyDetails(JourneyDetail journeyDetail);

    void handleNearbyStops(LocationList locationList);

    void handleDepartureBoard(DepartureBoard departureBoard);

    void handleError(String during_method, String error_msg);
}
