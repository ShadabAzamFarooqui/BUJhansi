package com.example.hp.stickpick.utils;

import android.location.Location;

/**
 * Created by Shadab Azam Farooqui on 14-Jan-18.
 */

public class DistanceCalculation {

    public static Double lat1,long1;

    public static Double getDistance(Double lat1,Double long1,Double lat2,Double long2){
        Location startPoint=new Location("locationA");
        startPoint.setLatitude(lat1);
        startPoint.setLongitude(long1);

        Location endPoint=new Location("locationA");
        endPoint.setLatitude(lat2);
        endPoint.setLongitude(long2);

        double distance=startPoint.distanceTo(endPoint);
        return distance;
    }
}
