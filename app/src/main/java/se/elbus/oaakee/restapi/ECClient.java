package se.elbus.oaakee.restapi;

import android.util.Base64;

import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import se.elbus.oaakee.restapi.ecmodel.busInfo;

public class EcClient {
    private static final String EC_API_URL = "https://ece01.ericsson.net:4443/";
    private final String CREDENTIALS = "grp31:C7CVFDHO48";
    private final String CREDENTIALS_BASE64 = "Basic " + Base64.encodeToString(CREDENTIALS.getBytes(), Base64.NO_WRAP);
    private ECCallback ec_callback;
    private mEcApi mEcApi;

    public EcClient(ECCallback ec_callback) {
        this.ec_callback = ec_callback;
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(EC_API_URL)
//                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        mEcApi = restAdapter.create(mEcApi.class);
    }

    // Gets sensor information from one/every bus
    // Example sensors (more at http://platform.goteborgelectricity.se/api/sensorer-och-resurser):
    // Ericsson$GPS
    // Ericsson$Stop_Pressed
    // Ericsson$Wlan_Connectivity
    //
    // Dates will be converted to unix epoch time in ms
    public void getBusSensor(String bus_ID_or_empty, Date start_time, Date end_time, String sensor) {
        if (!bus_ID_or_empty.isEmpty()) {
            mEcApi.getBusSensor(CREDENTIALS_BASE64, bus_ID_or_empty, Long.toString(start_time.getTime()), Long.toString(end_time.getTime()), sensor, new Callback<List<busInfo>>() {
                @Override
                public void success(List<busInfo> busInfos, Response response) {
                    ec_callback.handleSensorData(busInfos);
                }

                @Override
                public void failure(RetrofitError error) {
                    ec_callback.handleError("getBusSensor", error.getMessage());
                }
            });
        } else {
            mEcApi.getAllBusesSensors(CREDENTIALS_BASE64, Long.toString(start_time.getTime()), Long.toString(end_time.getTime()), sensor, new Callback<List<busInfo>>() {
                @Override
                public void success(List<busInfo> busInfos, Response response) {
                    ec_callback.handleSensorDataFromAllBuses(busInfos);
                }

                @Override
                public void failure(RetrofitError error) {
                    ec_callback.handleError("getBusSensor", error.getMessage());
                }
            });
        }
    }


    // Get a specific resource from one/every bus
    // Example resources:
    // Ericsson$Latitude_Value       - returns the Latitude the bus has travelled through between start and end time
    // Ericsson$Stop_Pressed_Value
    public void getBusResource(String bus_ID_or_empty, Date start_time, Date end_time, String resource) {
        if (!bus_ID_or_empty.isEmpty()) {
            mEcApi.getBusResource(CREDENTIALS_BASE64, bus_ID_or_empty, Long.toString(start_time.getTime()), Long.toString(end_time.getTime()), resource, new Callback<List<busInfo>>() {
                @Override
                public void success(List<busInfo> busInfos, Response response) {
                    ec_callback.handleResourceData(busInfos);
                }

                @Override
                public void failure(RetrofitError error) {
                    ec_callback.handleError("getBusResource", error.getMessage());
                }
            });
        } else {
            mEcApi.getAllBusesResources(CREDENTIALS_BASE64, Long.toString(start_time.getTime()), Long.toString(end_time.getTime()), resource, new Callback<List<busInfo>>() {
                @Override
                public void success(List<busInfo> busInfos, Response response) {
                    ec_callback.handleResourceDataFromAllBuses(busInfos);
                }

                @Override
                public void failure(RetrofitError error) {
                    ec_callback.handleError("getBusResource", error.getMessage());
                }
            });
        }
    }

    private interface mEcApi {

        // Will request a GET like: ?dgw=Ericsson$Vin_Num_001&t1=1443100184000&t2=1443107384000&sensorSpec=Ericsson$GPS
        @GET("/ecity")
        void getBusSensor(@Header("Authorization") String authorization, @Query("dgw") String bus_ID, @Query("t1") String time1, @Query("t2") String time2, @Query("sensorSpec") String sensor, Callback<List<busInfo>> cb);

        //Bus ID omitted - get sensor for all buses
        @GET("/ecity")
        void getAllBusesSensors(@Header("Authorization") String authorization, @Query("t1") String time1, @Query("t2") String time2, @Query("sensorSpec") String sensor, Callback<List<busInfo>> cb);

        //Will request a GET like: ?dgw=Ericsson$Vin_Num_001&t1=1443100184000&t2=1443107384000&resourceSpec=Ericsson$Latitude_Value
        @GET("/ecity")
        void getBusResource(@Header("Authorization") String authorization, @Query("dgw") String bus_ID, @Query("t1") String time1, @Query("t2") String time2, @Query("resourceSpec") String resource, Callback<List<busInfo>> cb);

        //Bus ID omitted - get resources for all buses
        @GET("/ecity")
        void getAllBusesResources(@Header("Authorization") String authorization, @Query("t1") String time1, @Query("t2") String time2, @Query("resourceSpec") String resource, Callback<List<busInfo>> cb);

    }
}
