package com.thereza.cmedtask01.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by theReza on 8/2/2018.
 */

public class ApiClient {

    private static Retrofit retrofit = null;

    public static ApiInterface getApiClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(HttpParams.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiInterface.class);
    }
}
