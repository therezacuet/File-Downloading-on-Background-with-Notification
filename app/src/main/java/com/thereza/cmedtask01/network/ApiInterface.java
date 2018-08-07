package com.thereza.cmedtask01.network;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;

/**
 * Created by theReza on 8/2/2018.
 */

public interface ApiInterface {

    @GET(HttpParams.API_END_POINT)
    @Streaming
    Call<ResponseBody> downloadFile();

}
