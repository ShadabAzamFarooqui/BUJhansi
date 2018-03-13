package com.example.hp.stickpick.android_retrofit.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by BerylSystems on 18-Dec-17.
 */

public class ApiClient {

    public static String  BASE_URL = "https://maps.googleapis.com/maps/";

    public static ApiInterface getApiInterface(){
        Retrofit retrofit=null;
        if (retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        ApiInterface service = retrofit.create(ApiInterface.class);
        return service;
    }




}
