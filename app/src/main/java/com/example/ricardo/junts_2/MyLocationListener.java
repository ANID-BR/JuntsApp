package com.example.ricardo.junts_2;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by joao on 28/01/16.
 */
public class MyLocationListener implements LocationListener {

    private Context context;

    public static HashMap localizacaoAtual;

    public MyLocationListener(Context context) {
        this.context = context;
    }


    @Override
    public void onLocationChanged(Location location) {
        localizacaoAtual = new HashMap(2);
        localizacaoAtual.put("latitude", location.getLatitude());
        localizacaoAtual.put("longitude", location.getLongitude());

        //Toast.makeText(this.context, "Location changed: Lat: " + location.getLatitude() + " Lng: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
