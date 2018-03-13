package com.example.hp.stickpick.android_retrofit.api;

import com.example.hp.stickpick.activity.HomeActivity;
import com.example.hp.stickpick.android_retrofit.network.request.GetRequest;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by BerylSystems on 10-Feb-18.
 */

public class ApiActionCalls {
    public static void action(String action) {

        if (action.equals(Cv.ACTION_MOSQUE_REQUEST)) {
            handleMosqueRequest("mosque");
        } else if (action.equals(Cv.ACTION_MOSQUE_REQUEST)) {

        }

    }

    public static void handleMosqueRequest(String type) {

        Call<GetRequest> call = ApiClient.getApiInterface().getNearbyPlaces(type, HomeActivity.lats + "," + HomeActivity.lngs, 1000);
        call.enqueue(new Callback<GetRequest>() {
            @Override
            public void onResponse(Call<GetRequest> call, Response<GetRequest> response) {
                if (response.code() == 200) {
                    GetRequest body = response.body();
                    EventBus.getDefault().post(body);
                } else {
                    EventBus.getDefault().post(Cv.TIMEOUT);
                }

            }

            @Override
            public void onFailure(Call<GetRequest> call, Throwable t) {

            }

        });
    }
}
