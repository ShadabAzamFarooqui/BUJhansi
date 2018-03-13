package com.example.hp.stickpick.android_retrofit.network.response;

/**
 * Created by BerylSystems on 19-Dec-17.
 */

public class GetMosqueResponce {
    public int status;
    public String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
