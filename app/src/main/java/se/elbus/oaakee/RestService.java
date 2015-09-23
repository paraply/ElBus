package se.elbus.oaakee;

import android.util.Base64;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by mike on 2015-09-23.
 */
public class RestService {
    final String ECITY_BASE_URL = "https://ece01.ericsson.net:4443/ecity";
    final String CREDENTIALS = "grp31:C7CVFDHO48";
    final String CREDENTIALS_BASE64 = "Basic " + Base64.encodeToString(CREDENTIALS.getBytes(), Base64.NO_WRAP);




    public RestService(){
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(ECITY_BASE_URL)
                .setClient(new OkClient(new OkHttpClient()));
    }

}
