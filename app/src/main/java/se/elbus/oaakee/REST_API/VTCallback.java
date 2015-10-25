package se.elbus.oaakee.REST_API;


import se.elbus.oaakee.REST_API.VT_Model.DepartureBoard;
import se.elbus.oaakee.REST_API.VT_Model.JourneyDetail;
import se.elbus.oaakee.REST_API.VT_Model.LocationList;

/**
 * Created by paraply on 2015-10-05.
 */
public interface VTCallback {
    void got_journey_details(JourneyDetail journeyDetail);

    void got_nearby_stops(LocationList locationList);

    void got_departure_board(DepartureBoard departureBoard);

    void got_error(String during_method, String error_msg);
}
