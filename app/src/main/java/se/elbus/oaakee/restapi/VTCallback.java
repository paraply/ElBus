package se.elbus.oaakee.restapi;


import se.elbus.oaakee.restapi.vtmodel.DepartureBoard;
import se.elbus.oaakee.restapi.vtmodel.JourneyDetail;
import se.elbus.oaakee.restapi.vtmodel.LocationList;

public interface VTCallback {
    void got_journey_details(JourneyDetail journeyDetail);

    void got_nearby_stops(LocationList locationList);

    void got_departure_board(DepartureBoard departureBoard);

    void got_error(String during_method, String error_msg);
}
