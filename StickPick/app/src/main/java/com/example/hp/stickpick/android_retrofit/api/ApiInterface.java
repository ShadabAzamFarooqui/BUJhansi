package com.example.hp.stickpick.android_retrofit.api;



import com.example.hp.stickpick.android_retrofit.network.request.GetRequest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Created by BerylSystems on 18-Dec-17.
 */

public interface ApiInterface {

    @GET("api/place/nearbysearch/json?sensor=true&key=AIzaSyDN7RJFmImYAca96elyZlE5s_fhX-MMuhk")
    Call<GetRequest> getNearbyPlaces(@Query("type") String type, @Query("location") String location, @Query("radius") int radius);

}
