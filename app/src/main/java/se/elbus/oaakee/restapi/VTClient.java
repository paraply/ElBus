package se.elbus.oaakee.restapi;


import se.elbus.oaakee.restapi.vtmodel.DepartureBoard;
import se.elbus.oaakee.restapi.vtmodel.JourneyDetail;
import se.elbus.oaakee.restapi.vtmodel.JourneyDetailRef;
import se.elbus.oaakee.restapi.vtmodel.LocationList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.SimpleXMLConverter;
import retrofit.http.GET;
import retrofit.http.Query;

// ********* EXAMPLE USAGE:

//public class MainActivity extends AppCompatActivity implements VtCallback {
//    VtClient vast;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        vast = new VtClient(this);
//        vast.get_nearby_stops("57.703834&", "11.966404", "30", "1000");
//    }
//
//    @Override
//    public void handleJourneyDetails(JourneyDetail journeyDetail) {
//        for (Stop s : journeyDetail.stop) {
//            Log.i("### LINE STOPS @", s.name + " WHEN: " + s.arrTime);
//        }
//    }
//
//    @Override
//    public void handleNearbyStops(LocationList locationList) {
//        for (StopLocation s : locationList.stoplocation) { // List all nearby stops
//            Log.i("### NEAR STOP", s.name + " ID:" + s.id + " TRACK:" +  s.track );
//        }
//        StopLocation closest = locationList.stoplocation.get(0); // The closest stop is at the top of the list
//        Log.i("### CLOSEST STOP", closest.name + " ID:" + closest.id + " TRACK:" +  closest.track );
//        vast.get_departure_board(closest.id); // Get departures from this stop
//    }
//
//    @Override
//    public void handleDepartureBoard(DepartureBoard departureBoard) {
//
//        for (Departure d : departureBoard.departure) { // List all the departures from this stop
//            Log.i("### DEPARTURES: ", d.name  + " SHORT NAME: " + d.sname + " DIRECTION: " + d.direction);
//            if (d.sname.equals("11") && d.direction.equals("Bergsjön")){ // If spårvagn 11 mot Bergsjön
//                vast.get_journey_details(d.journeyDetailRef); // Get journey details from this journey
//                return;
//            }
//        }
//    }
//
//    @Override
//    public void handleError(String during_method, String error_msg) {
//        Log.i("### ERR", "during: " + during_method + "-" +  error_msg);
//    }
//}


public class VtClient {
    private static final String API_KEY = "47befa35-9616-4ee0-af17-b82dd53e8e1c";
    private static final String VT_API_URL = "http://api.vasttrafik.se/bin/rest.exe/v1";
    VtCallback vt_callback;
    private VTApi vt_api;

    public VtClient(VtCallback vt_callback) {
        this.vt_callback = vt_callback;
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(VT_API_URL)
//                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new SimpleXMLConverter())
                .build();

        vt_api = restAdapter.create(VTApi.class);
    }

//     Given a station ID returns all departures from that station
//
//     Example usage:
//          VtClient vast = new VtClient();
//          vast.get_station_board("9021014031336000");

    public void get_departure_board(String stop_id) {
        vt_api.api_get_departure_board(stop_id, new Callback<DepartureBoard>() {
            @Override
            public void success(DepartureBoard departureBoard, Response response) {
                vt_callback.handleDepartureBoard(departureBoard);
            }

            @Override
            public void failure(RetrofitError error) {
                vt_callback.handleError("get_departure_board", error.getMessage());
            }
        });
    }

//    Get the stops from a line
//    Needs a JourneyDetailRef from DepartureBoard (or trip if ever implemented)

    public void get_journey_details(JourneyDetailRef jref) {
        String trimmed_url = jref.getRef().substring((VT_API_URL + "/journeyDetail?ref=").length()); // remove http://api.vasttrafik.se/bin/rest.exe/v1/journeyDetail?ref=

        vt_api.api_get_journey_detail(trimmed_url, new Callback<JourneyDetail>() {
            @Override
            public void success(JourneyDetail journeyDetail, Response response) {
                vt_callback.handleJourneyDetails(journeyDetail);
            }

            @Override
            public void failure(RetrofitError error) {
                vt_callback.handleError("get_journey_details", error.getMessage());
            }
        });

    }

//        Get nearby stops from latitude, longitude.
//        Limit the amount of results using max_results
//        Limit the distance in meters using max_distance
//        Returns a LocationList object if successful
//        Sorted by: closest stop first

//        Example usage:
//          VtClient vast = new VtClient();
//          vast.get_nearby_stops("57.703834&", "11.966404", "30", "1000");

    public void get_nearby_stops(String lat, String lon, String max_results, String max_distance) {
        vt_api.api_get_LocationList(lat, lon, max_results, max_distance, new Callback<LocationList>() {

            @Override
            public void success(LocationList locationList, Response response) {
                vt_callback.handleNearbyStops(locationList);
            }

            @Override
            public void failure(RetrofitError error) {
                vt_callback.handleError("get_nearby_stops", error.getMessage());
            }
        });
    }


    public interface VTApi {

        @GET("/location.nearbystops?authKey=" + API_KEY)
            //use "&format=json" to get json replies otherwise xml as default
        void api_get_LocationList(@Query("originCoordLat") String latitude, @Query("originCoordLong") String longitude, @Query("maxNo") String max_results, @Query("maxDist") String max_distance, Callback<LocationList> cb);

        @GET("/departureBoard?authKey=" + API_KEY + "&excludeDR=1&timeSpan=60")
        void api_get_departure_board(@Query("id") String station_ID, Callback<DepartureBoard> cb);

        @GET("/journeyDetail")
        void api_get_journey_detail(@Query("ref") String path, Callback<JourneyDetail> cb);

    }


}
