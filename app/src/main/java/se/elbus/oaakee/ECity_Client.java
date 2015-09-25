package se.elbus.oaakee;

import android.util.Base64;
import android.util.Log;

import java.util.Date;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;

/**
 * Created by mike on 2015-09-23.
 */
public class ECity_Client {
    private static final String ECAPI_URL = "https://ece01.ericsson.net:4443/";
    final String CREDENTIALS = "grp31:C7CVFDHO48";
    final String CREDENTIALS_BASE64 = "Basic " + Base64.encodeToString(CREDENTIALS.getBytes(), Base64.NO_WRAP);
    private static ECity_API ecity_api;

    public ECity_Client(){
        if (ecity_api == null){

            Retrofit client = new Retrofit.Builder()
                    .baseUrl(ECAPI_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ecity_api = client.create(ECity_API.class);
        }
    }

    public void get_bus_sensor(String bus_ID_or_empty, Date start_time, Date end_time, String sensor ){
        Call<List<Bus_info>> call;
        if (bus_ID_or_empty.isEmpty()){
            call = ecity_api.get_all_buses_sensors(CREDENTIALS_BASE64, Long.toString(start_time.getTime()), Long.toString(end_time.getTime()), sensor);
            Log.i("### get_all_buses_sens", " time1:" + Long.toString(start_time.getTime()) + " time2:" + Long.toString(end_time.getTime()));
        }else{
            call = ecity_api.get_bus_sensor(CREDENTIALS_BASE64, bus_ID_or_empty, Long.toString(start_time.getTime()), Long.toString(end_time.getTime()), sensor);
            Log.i("### get_bus_sensor", "ID:" + bus_ID_or_empty + " time1:" + Long.toString(start_time.getTime()) + " time2:" + Long.toString(end_time.getTime()) );
        }

        call.enqueue(new Callback<List<Bus_info>>() {
            @Override
            public void onResponse(retrofit.Response<List<Bus_info>> response) {
                if (response.isSuccess()) {
                    List<Bus_info> result = response.body();
                    for (Bus_info res : result){
                        Log.i("### SNS RESULT", "BUS ID:" +  res.gatewayId + " RESOURCE:" + res.resourceSpec + " VALUE:" + res.value + " TIME:" + res.timestamp  );
                    }
                }else {
                        Log.i("### ERR", "get_bus_sensor response.isSuccess returned false");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("### ERR get_bus_sensor",t.getMessage());
            }
        });
    }


    private interface ECity_API{

        // Will request a GET like: ?dgw=Ericsson$Vin_Num_001&t1=1443100184000&t2=1443107384000&sensorSpec=Ericsson$GPS
        @GET("ecity")
        Call<List<Bus_info>> get_bus_sensor(@Header("Authorization") String authorization, @Query("dgw") String bus_ID, @Query("t1") String time1, @Query("t2") String time2, @Query("sensorSpec") String sensor );

        //Bus ID omitted - get sensor for all buses
        @GET("ecity")
        Call<List<Bus_info>> get_all_buses_sensors(@Header("Authorization") String authorization, @Query("t1") String time1, @Query("t2") String time2, @Query("sensorSpec") String sensor );


        //Will request a GET like: ?dgw=Ericsson$Vin_Num_001&t1=1443100184000&t2=1443107384000&resourceSpec=Ericsson$Latitude_Value
        @GET("ecity")
        Call<List<Bus_info>> get_bus_resource(@Header("Authorization") String authorization, @Query("dgw") String bus_ID, @Query("t1") String time1, @Query("t2") String time2, @Query("resourceSpec") String resource );

        //Bus ID omitted - get resources for all buses
        @GET("ecity")
        Call<List<Bus_info>> get_all_buses_resources(@Header("Authorization") String authorization,  @Query("t1") String time1, @Query("t2") String time2, @Query("resourceSpec") String resource );

    }
}
