package se.elbus.oaakee;

import android.util.Base64;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
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
            OkHttpClient okClient = new OkHttpClient();
            okClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response response = chain.proceed(chain.request());
                    return response;
                }
            });

            Retrofit client = new Retrofit.Builder()
                    .baseUrl(ECAPI_URL)
                    .client(okClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ecity_api = client.create(ECity_API.class);
        }
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
